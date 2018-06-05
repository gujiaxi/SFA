// Copyright (c) 2017 - Patrick Schäfer (patrick.schaefer@hu-berlin.de)
// Distributed under the GLP 3.0 (See accompanying file LICENSE)
package sfa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfa.classification.*;
import sfa.timeseries.MultiVariateTimeSeries;
import sfa.timeseries.TimeSeries;
import sfa.timeseries.TimeSeriesLoader;
import sfa.transformation.MUSE;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@RunWith(JUnit4.class)
//public class MyMUSETest extends AbsClassifierTest {
public class MyMUSETest {

  //@Override
  //protected List<DataSet> getDataSets() {
      //List<DataSet> dataSets = new ArrayList<>();
      //dataSets.add(new DataSet("DigitShapeRandom"));
      //return dataSets;
  //}

  //@Override
  //protected Classifier initClassifier() {
      //return new MUSEClassifier();
  //}

  @Test
  public void testSave() {
      try {
          //File savedMuse = new File("/home/jiaxi/Desktop/savedMuse.model");
          MUSEClassifier muse = museClassify("MYMTS");
          //muse.save(savedMuse);
          //Classifier.load(savedMuse);
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public MUSEClassifier museClassify(String ds) throws IOException {
    MUSEClassifier muse= new MUSEClassifier();
    ClassLoader classLoader = SFAWordsTest.class.getClassLoader();
    File dir = new File(classLoader.getResource("datasets/multivariate").getFile());
    File d = new File(dir.getAbsolutePath() + "/" + ds);
    if (d.exists() && d.isDirectory()) {
      for (File train : d.listFiles()) {
        if (train.getName().toUpperCase().endsWith("TRAIN3")) {
          File test = new File(train.getAbsolutePath().replaceFirst("TRAIN3", "TEST3"));

          if (!test.exists()) {
            System.err.println("File " + test.getName() + " does not exist.");
            test = null;
          }

          boolean useDerivatives = true;
          MultiVariateTimeSeries[] trainSamples = TimeSeriesLoader.loadMultivariateDatset(train, useDerivatives);
          MultiVariateTimeSeries[] testSamples = TimeSeriesLoader.loadMultivariateDatset(test, useDerivatives);

          MUSEClassifier.BIGRAMS = true;
          MUSEClassifier.MAX_WINDOW_LENGTH = 100;
          MUSEClassifier.Score score = new MUSEClassifier.Score();
          score = muse.fit(trainSamples);

          // by jiaxi
          System.out.println("------------------------");
          int[] testResults = new int[4];
          testResults = muse.iScore(testSamples);
          System.out.print("TP: " + testResults[0] + "\n" +
                           "TN: " + testResults[1] + "\n" +
                           "FP: " + testResults[2] + "\n" +
                           "FN: " + testResults[3] + "\n");
          System.out.println("------------------------");
          //int correctTesting = muse.score(testSamples).correct.get();
          //MUSEClassifier.Score museScore = new MUSEClassifier.Score(
            //"WEASEL+MUSE", correctTesting, testSamples.length,
            //score.training, trainSamples.length,
            //score.windowLength);
          //System.out.println(ds + ";" + museScore.toString());
        }
      }
    } else {
      System.out.println("Dataset could not be found: " + d.getAbsolutePath() + ".");
    }
    return muse;
  }
}
