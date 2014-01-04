package edu.umass.cs.iesl.wallach.hierarchical;

import java.io.*;
import java.util.*;
import gnu.trove.*;
import cc.mallet.types.*;
import cc.mallet.util.Maths;

public class HLDAExpt {

  public static void main(String[] args) throws java.io.IOException {

    if (args.length != 9) {
      System.out.println("Usage: HLDAExpt <instances> <num_topics> <path> <num_itns> <print_interval> <save_sample_interval> <optimize> <output_dir>");
      System.exit(1);
    }

    String file = args[0];

    int T = Integer.parseInt(args[1]); // # of topics

    String path = args[2]; // path assumption (minimal, maximal, gibbs)

    int numItns = Integer.parseInt(args[3]); // # Gibbs iterations
    int printInterval = Integer.parseInt(args[4]); // # iterations between printing out topics
    int saveSampleInterval = Integer.parseInt(args[5]);

    assert args[6].length() == 2;
    boolean[] symmetric = new boolean[2];

    for (int i=0; i<2; i++)
      switch(args[6].charAt(i)) {
      case '0': symmetric[i] = false; break;
      case '1': symmetric[i] = true; break;
      default: System.exit(1);
      }

    assert args[7].length() == 2;
    boolean[] optimize = new boolean[2]; // whether to optimize hyperparameters

    for (int i=0; i<2; i++)
      switch(args[7].charAt(i)) {
      case '0': optimize[i] = false; break;
      case '1': optimize[i] = true; break;
      default: System.exit(1);
      }

    String outputDir = args[8]; // output directory

    // load data

    InstanceList docs = InstanceList.load(new File(file));

    Alphabet wordDict = docs.getDataAlphabet();

    System.out.println("Data loaded.");

    int W = wordDict.size();

    double alpha = 0.1 * T;
    double beta = 0.01 * W;

    // form output filenames

    String optionsFile = outputDir + "/options.txt";

    String documentTopicsFile = outputDir + "/doc_topics.txt";
    String topicWordsFile = outputDir + "/topic_words.txt";
    String stateFile = outputDir + "/state.txt";
    String alphaFile = outputDir + "/alpha.txt";
    String betaFile = outputDir + "/beta.txt";
    String logProbFile = outputDir + "/log_prob.txt";
    String hyperparamFile = outputDir + "/hyperparam.txt";

    PrintWriter pw = new PrintWriter(optionsFile, "UTF-8");

    pw.println("Data = " + file);

    int corpusLength = 0;

    for (int d=0; d<docs.size(); d++) {

      FeatureSequence fs = (FeatureSequence) docs.get(d).getData();
      corpusLength += fs.getLength();
    }

    pw.println("Tokens in data = " + corpusLength);

    pw.println("T = " + T);
    pw.println("Path assumption = " + path);
    pw.println("# iterations = " + numItns);
    pw.println("Print topics interval = " + printInterval);
    pw.println("Save sample interval = " + saveSampleInterval);
    pw.println("Symmetric alpha = " + symmetric[0]);
    pw.println("Symmetric beta = " + symmetric[1]);
    pw.println("Optimize alpha = " + optimize[0]);
    pw.println("Optimize beta = " + optimize[1]);
    pw.println("Date = " + (new Date()));

    pw.close();

    HLDA lda = new HLDA();

    lda.estimate(docs, T, path, alpha, beta, numItns, printInterval, saveSampleInterval, symmetric, optimize, documentTopicsFile, topicWordsFile, stateFile, alphaFile, betaFile, logProbFile, hyperparamFile);

  }
}
