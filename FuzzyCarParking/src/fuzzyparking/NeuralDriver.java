package fuzzyparking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

public class NeuralDriver {

	private final int inputN = 2;
	private double positionMax = 0;
	private double positionMin = Double.MAX_VALUE;
	private double phiMax = 0;
	private double phiMin = Double.MAX_VALUE;
	private double thetaMax = 0;
	private double thetaMin = Double.MAX_VALUE;
	private String rawDataFilePath;
	private double learningRate;
	private NeuralNetwork neuralNetwork;

	private int entriesN = 0;
	private int threshold = 0;

	private String learningDataFilePath = "input/learningData.csv";
	private String neuralNetworkModelFilePath = "neuralDriver.nnet";

	public NeuralDriver(double learningRate, String rawDataFilePath) {
		this.learningRate = learningRate;
		this.rawDataFilePath = rawDataFilePath;
	}
	
	public NeuralNetwork getNetwork() {
		if(neuralNetwork == null) {
			neuralNetwork = NeuralNetwork.createFromFile(
	                neuralNetworkModelFilePath);
		}
		return neuralNetwork;
	}

	public void splitData() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(rawDataFilePath));
		threshold = 0;
		try {			
			while (reader.readLine() != null) {
				entriesN++;
			}
		} finally {
			reader.close();
		}
		threshold = (int) Math.round(entriesN * 0.6);

		reader = new BufferedReader(new FileReader(rawDataFilePath));
		BufferedWriter trainingWriter = new BufferedWriter(new FileWriter("input/rawTrainingData.csv"));
		BufferedWriter testingWriter = new BufferedWriter(new FileWriter("input/rawTestingData.csv"));

		try {
			String line;
			for (int i = 0; i < threshold; i++) {
				trainingWriter.write(reader.readLine());
				trainingWriter.newLine();
			}
			
			while ((line = reader.readLine()) != null) {
				testingWriter.write(line);
				testingWriter.newLine();
			}

		} finally {
			reader.close();
			trainingWriter.close();
			testingWriter.close();
		}

	}

	public void prepareData() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(rawDataFilePath));

		// max and min for x
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");

				double currentValue = Double.valueOf(tokens[0]);
				if (currentValue > positionMax) {
					positionMax = currentValue;
				}
				if (currentValue < positionMin) {
					positionMin = currentValue;
				}

				currentValue = Double.valueOf(tokens[1]);
				if (currentValue > phiMax) {
					phiMax = currentValue;
				}
				if (currentValue < phiMin) {
					phiMin = currentValue;
				}

				currentValue = Double.valueOf(tokens[2]);
				if (currentValue > thetaMax) {
					thetaMax = currentValue;
				}
				if (currentValue < thetaMin) {
					thetaMin = currentValue;
				}
			}
		} finally {
			reader.close();
		}

		splitData();

		reader = new BufferedReader(new FileReader("input/rawTrainingData.csv"));
		BufferedWriter writer = new BufferedWriter(new FileWriter(learningDataFilePath));

		LinkedList<Double> valuesQueue = new LinkedList<Double>();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				double currentValue = Double.valueOf(line.split(",")[0]);
				// normalize value and add it to queue
				double normalizedValue = normalizeValue(0, currentValue);
				valuesQueue.add(normalizedValue);

				currentValue = Double.valueOf(line.split(",")[1]);
				normalizedValue = normalizeValue(1, currentValue);
				valuesQueue.add(normalizedValue);

				currentValue = Double.valueOf(line.split(",")[2]);
				normalizedValue = normalizeValue(2, currentValue);
				valuesQueue.add(normalizedValue);

				System.out.println(valuesQueue.toString());
				String valueLine = valuesQueue.toString().replaceAll("\\[|\\]", "");
				System.out.println(valueLine);
				writer.write(valueLine);
				writer.newLine();

				// clear values queue
				valuesQueue.clear();
			}
		} finally {
			reader.close();
			writer.close();
		}
	}

	public double normalizeValue(int type, double value) {

		double max = 0.0;
		double min = 0.0;
		switch (type) {
		case 0:
			max = positionMax;
			min = positionMin;
			break;
		case 1:
			max = phiMax;
			min = phiMin;
			break;
		case 2:
			max = thetaMax;
			min = thetaMin;
			break;
		}
		return ((value - min) / (max - min));
	}

	public double denormalizeValue(int type, double value) {

		double max = 0.0;
		double min = 0.0;
		switch (type) {
		case 0:
			max = positionMax;
			min = positionMin;
			break;
		case 1:
			max = phiMax;
			min = phiMin;
			break;
		case 2:
			max = thetaMax;
			min = thetaMin;
			break;
		}
		return (value * (max - min) + min); 
	}

	public void trainNetwork() throws IOException {
		NeuralNetwork<BackPropagation> neuralNetwork = new MultiLayerPerceptron(inputN, inputN*2 + 1, 1);

		int maxIterations = 5000;
		double maxError = 0.00001;
		SupervisedLearning learningRule = neuralNetwork.getLearningRule();
		learningRule.setMaxError(maxError);
		learningRule.setLearningRate(learningRate);
		learningRule.setMaxIterations(maxIterations);
		learningRule.addListener(new LearningEventListener() {
			public void handleLearningEvent(LearningEvent learningEvent) {
				SupervisedLearning rule = (SupervisedLearning) learningEvent.getSource();
				System.out.println("Network error for iteration " + rule.getCurrentIteration() + ": "
						+ rule.getTotalNetworkError());
//				try {
//				if(testNetwork(neuralNetwork) <= 0.0001) {
//					neuralNetwork.stopLearning();
//					System.out.println("Training halted");
//				}
//				} catch(IOException e) {
//					e.printStackTrace();
//				}
			}
		});
		DataSet trainingSet = loadTrainingData(learningDataFilePath);
		neuralNetwork.learn(trainingSet);
		neuralNetwork.save(neuralNetworkModelFilePath);
	}

	DataSet loadTrainingData(String filePath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		DataSet trainingSet = new DataSet(inputN, 1);

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");

				double trainValues[] = new double[inputN];
				for (int i = 0; i < inputN; i++) {
					trainValues[i] = Double.valueOf(tokens[i]);
				}
				double expectedValue[] = new double[] { Double.valueOf(tokens[inputN]) };
				trainingSet.addRow(new DataSetRow(trainValues, expectedValue));
			}
		} finally {
			reader.close();
		}
		return trainingSet;
	}

	public double testNetwork(NeuralNetwork<BackPropagation> network) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("input/rawTestingData.csv"));
		LinkedList<Double> valuesQueue = new LinkedList<>();
		int samplesN = entriesN - threshold;
		int index = (int) (Math.random() * samplesN);
		String[] tokens = new String[3];
		try {
			for (int i = 0; i < samplesN; i++) {
				String line = reader.readLine();
				if (i == index) {
					tokens = line.split(",");
					break;
				}
			}
			for (int i = 0; i < 3; i++)
				valuesQueue.add(Double.valueOf(tokens[i]));
		} finally {
			reader.close();
		}

		double expectedValue = valuesQueue.remove(2);
		
		for (int i = 0; i < valuesQueue.size(); i++) {
			valuesQueue.set(i, normalizeValue(i, valuesQueue.get(i)));
		}

		double[] inputs = new double[valuesQueue.size()];
		for (int i = 0; i < valuesQueue.size(); i++)
			inputs[i] = valuesQueue.get(i);
		network.setInput(inputs);
		network.calculate();
		double[] networkOutput = network.getOutput();
		System.out.println("Expected value: " + expectedValue);
		System.out.println("Predicted value: " + denormalizeValue(2, networkOutput[0]));
		return (expectedValue - denormalizeValue(2, networkOutput[0]))
				* (expectedValue - denormalizeValue(2, networkOutput[0]));
	}
	
	public void drive() {
		NeuralNetwork neuralNetwork = getNetwork();
		double[] inputs = new double[2];
//		inputs[0] = normalizeValue(0, ParkingLot.getInstance().getPosError());
		inputs[0] = normalizeValue(0, ParkingLot.getInstance().getCar().x);
		inputs[1] = normalizeValue(1, Math.toDegrees(ParkingLot.getInstance().getCar().phi));
		neuralNetwork.setInput(inputs);
		neuralNetwork.calculate();
		double output = 0.0;
		output = denormalizeValue(2, neuralNetwork.getOutput()[0]);
		ParkingLot.getInstance().getCar().theta = Math.toRadians(output);
	}

}
