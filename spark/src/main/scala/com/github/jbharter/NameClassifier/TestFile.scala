package com.github.jbharter.NameClassifier

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest

class ComplexNumber(var real: Double, var imag: Double) {
  def mag() : Double = {
    math.sqrt(math.pow(real, 2.0) + math.pow(imag, 2.0))
  }
  def flat() :Array[Double] = Array[Double](real,imag)
  def ifRealThen(pred: Double => Boolean, transform: Double => Double) : ComplexNumber = {
    if (pred.apply(real)) real = transform.apply(real)
    this
  }
  def ifImagThen(pred: Double => Boolean, transform: Double => Double) : ComplexNumber = {
    if (pred.apply(imag)) imag = transform.apply(imag)
    this
  }
  def bothAre(pred: Double => Boolean) : Boolean = realIs(pred) && imagIs(pred)
  def bothAreNot(pred: Double => Boolean) : Boolean = realIsNot(pred) && imagIsNot(pred)
  def realIs(pred: Double => Boolean) : Boolean = pred.apply(real)
  def realIsNot(pred: Double => Boolean) : Boolean = !pred.apply(real)
  def imagIs(pred: Double => Boolean) : Boolean = pred.apply(imag)
  def imagIsNot(pred: Double => Boolean) : Boolean = !pred.apply(imag)


  private def toMag(array: Array[ComplexNumber]): Array[Double] = array.map(_.mag())
  private def toFlat(array: Array[ComplexNumber]): Array[Double] = array.flatMap(_.flat())

  def toMag() : Function[Array[ComplexNumber],Array[Double]] = arr => toMag(arr)
  def toFlat() : Function[Array[ComplexNumber],Array[Double]] = arr => toFlat(arr)

  def trim(n: Int) : Function[Array[Double],Array[Double]] = arr => arr.map(BigDecimal(_).setScale(n,BigDecimal.RoundingMode.UP).toDouble)
  def trim() : Function[Array[Double],Array[Double]] = trim(2)
  def fromString(input: String): ComplexNumber = {
    // cleanse, assume imaginary, remove 'i's
    val str = (if (input.charAt(0) != '-') "+" else "").concat(input).replaceAll("i","")
    val operators = str.split("").zipWithIndex.filter(p => p._1 == "-" || p._1 == "+").map(_._2)
    val exps = str.split("").zipWithIndex.filter(p => p._1 == "e" || p._1 == "E").map(_._2)

    if (operators.length == 2) {
      new ComplexNumber(str.substring(0,operators(1)).toDouble,str.substring(operators(1)).toDouble)
    } else if (operators.length == 3) {
      // lead or lag exp
      val lead = exps.head < str.length - 4
      val left = if (lead) str.substring(0,operators(2)) else str.substring(0,operators(1))
      val right = if (lead) str.substring(operators(2)) else str.substring(operators(2))

      new ComplexNumber(left.toDouble,right.toDouble)
    } else if (operators.length == 4) {
      // double exp
      val left = str.substring(0,operators(2))
      val right = str.substring(operators(2))
      new ComplexNumber(left.toDouble,right.toDouble)
    } else {
      new ComplexNumber(Double.NegativeInfinity,Double.NegativeInfinity)
    }
  }

}

object ComplexNumber {

  private def toMag(array: Array[ComplexNumber]): Array[Double] = array.map(_.mag())
  private def toFlat(array: Array[ComplexNumber]): Array[Double] = array.flatMap(_.flat())

  def toMag() : Function[Array[ComplexNumber],Array[Double]] = arr => toMag(arr)
  def toFlat() : Function[Array[ComplexNumber],Array[Double]] = arr => toFlat(arr)

  def trim(n: Int) : Function[Array[Double],Array[Double]] = arr => arr.map(BigDecimal(_).setScale(n,BigDecimal.RoundingMode.UP).toDouble)
  def trim() : Function[Array[Double],Array[Double]] = trim(2)
  def fromString(input: String): ComplexNumber = {
    // cleanse, assume imaginary, remove 'i's
    val str = (if (input.charAt(0) != '-') "+" else "").concat(input).replaceAll("i","")
    val operators = str.split("").zipWithIndex.filter(p => p._1 == "-" || p._1 == "+").map(_._2)
    val exps = str.split("").zipWithIndex.filter(p => p._1 == "e" || p._1 == "E").map(_._2)

    if (operators.length == 2) {
      new ComplexNumber(str.substring(0,operators(1)).toDouble,str.substring(operators(1)).toDouble)
    } else if (operators.length == 3) {
      // lead or lag exp
      val lead = exps.head < str.length - 4
      val left = if (lead) str.substring(0,operators(2)) else str.substring(0,operators(1))
      val right = if (lead) str.substring(operators(2)) else str.substring(operators(2))

      new ComplexNumber(left.toDouble,right.toDouble)
    } else if (operators.length == 4) {
      // double exp
      val left = str.substring(0,operators(2))
      val right = str.substring(operators(2))
      new ComplexNumber(left.toDouble,right.toDouble)
    } else {
      new ComplexNumber(Double.NegativeInfinity,Double.NegativeInfinity)
    }
  }
}

class Row(nameComponents: Array[Double],
          classification : Int,
          ftData : Array[ComplexNumber])
{
  def mapNC(function: Function[Double,Double]): Row = {
    nameComponents.map(function)
    this
  }
  def mapFT[T](function: Function[ComplexNumber,T]): Row = {
    ftData.map(function)
    this
  }
  def forallFT(pred: ComplexNumber => Boolean): Boolean = ftData.forall(pred)
  def categorize() : LabeledPoint = {
    LabeledPoint(classification.toDouble,Vectors.dense(nameComponents++ftData.flatMap(_.flat())))
  }
  def categorize(transform: Function[Array[ComplexNumber],Array[Double]]) : LabeledPoint = {
    LabeledPoint(classification.toDouble,Vectors.dense(nameComponents++transform.apply(ftData)))
  }
  def fromProcessedNameStringRows(s:String): Array[Row] = {
    // Get Splits
    val rowArr = s.split(",")
    val nc = rowArr.slice(0,33).map(_.toDouble)
    val ft = rowArr.slice(168,202).map(e => ComplexNumber.fromString(e))

    // Single "row" for each classification
    rowArr.slice(33,168)
      .map(_.toDouble)
      .zipWithIndex.filter(_._1 == 1).map(_._2)
      .map(e => new Row(nc,e,ft))
  }
}

object Row {
  def fromProcessedNameStringRows(s:String): Array[Row] = {
    // Get Splits
    val rowArr = s.split(",")
    val nc = rowArr.slice(0,33).map(_.toDouble)
    val ft = rowArr.slice(168,202).map(e => ComplexNumber.fromString(e))

    // Single "row" for each classification
    rowArr.slice(33,168)
      .map(_.toDouble)
      .zipWithIndex.filter(_._1 == 1).map(_._2)
      .map(e => new Row(nc,e,ft))
  }
}

object TestFile extends App {

  //var nameCategoryFFTDatas = sc.textFile("matprocessedNames.csv").mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter } //.collect()

   // val config = new SparkConf
   //   config.setMaster("local[*]")
   //   config.setAppName("remote scala-spark exec jharte")

    //val sc = new SparkContext(config)
    //val local_file = new BufferedReader(new InputStreamReader(new FileInputStream("resources/procDataMassaged.csv"))).lines().collect(Collectors.toList())

  //if (!FileSystem.get(sc.hadoopConfiguration).exists(new Path("resources/massagedMatProcessedNamesSVM"))) {
    // gather ze datas
    // massage ze datas
    // chop off column headers
     //nameCategoryFFTData =

   // MLUtils.saveAsLibSVMFile(nameCategoryFFTData,"resources/massagedMatProcessedNamesSVM")
  //}

  //val data = nameCategoryFFTData//MLUtils.loadLibSVMFile(sc,"resources/massagedMatProcessedNamesSVM")

//    val data = sc.textFile("resources/procDataMassaged.csv")
//      .map(s => s.split(","))
//      .map( arr => arr.map(_.toDouble))
//      .map(arr => LabeledPoint(arr(0),Vectors.dense(arr.slice(1,arr.length))))
  // store
  //MLUtils.saveAsLibSVMFile(data,"data/normlibSVM.txt")
  // how many negative infinites do we have?
  //data.map()
  // truncate
  // data.map(s => new LabeledPoint(s.label,Vectors.dense(s.features.compressed.toArray.map(dub => BigDecimal(dub).setScale(1,BigDecimal.RoundingMode.CEILING).toDouble))))

  //for(i <- 100 to 500) {


  //}

  val sc = SparkContext.getOrCreate()
  var data = sc.textFile("other").mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter } //sc.parallelize(nameCategoryFFTDatas.slice(1,nameCategoryFFTDatas.length))
    // split on comma
    .flatMap((s:String) => Row.fromProcessedNameStringRows(s).iterator)
    .filter(r => r.forallFT(_.bothAreNot(_ == Double.NegativeInfinity)))
    .map(r => r.categorize(ComplexNumber.toMag().andThen(ComplexNumber.trim())))

  val splits = data.randomSplit(Array(0.7, 0.3))
  val (trainingData, testData) = (splits(0), splits(1))

  val numClasses = data.map(lp => lp.label).distinct().count().toInt
  val categoricalFeaturesInfo = Map[Int,Int]()
  val maxBins = numClasses
  val featureSubsetStrategy = "auto"
  val impurity = "gini"
  impurity.indexOf(",")

  val numTrees = 500

  val maxDepth = 20
  val model = RandomForest.trainClassifier(trainingData.toJavaRDD(),numClasses,categoricalFeaturesInfo,numTrees,featureSubsetStrategy,impurity,maxDepth,maxBins,0)

  // Evaluate model on test instances and compute test error
  val labelAndPreds = testData.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }
  val testMSE = labelAndPreds.map{ case(v, p) => math.pow(v - p, 2)}.mean()

  val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
  println("Test Error = " + testErr)
  println("Test MSE = " + testMSE)

  //save model
  model.save(sc, s"models/rfcm-$numTrees-$maxDepth-${BigDecimal(testErr).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble}")

}
