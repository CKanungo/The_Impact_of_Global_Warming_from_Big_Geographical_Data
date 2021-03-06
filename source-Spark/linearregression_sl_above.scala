import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.rdd._

// Load and parse the data
val data = sc.textFile("../datasets/time_temp_sl_above.csv")
val parsedData = data.map { line =>
  val parts = line.split(',')
  LabeledPoint(parts.last.toDouble, Vectors.dense(parts.take(2).map(_.toDouble)))
}

// Build linear regression model
var regression = new LinearRegressionWithSGD()
regression.optimizer.setStepSize(0.0001)
val model = regression.run(parsedData)

val valuesAndPreds = parsedData.map{ point =>
  val prediction = model.predict(point.features)
  (prediction, point.label)
}


val metrics = new RegressionMetrics(valuesAndPreds)

// Squared error
println(s"MSE = ${metrics.meanSquaredError}")
println(s"RMSE = ${metrics.rootMeanSquaredError}")

// R-squared
println(s"R-squared = ${metrics.r2}")


// Export linear regression model to PMML
model.toPMML("../exported_pmml_models/linearregression_above.xml")

// Test model on training data

var predictedValue = model.predict(Vectors.dense(100, 1.7))
// Random temp
predictedValue = model.predict(Vectors.dense(400, 5))



