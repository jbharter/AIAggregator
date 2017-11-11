import org.apache.spark.{SparkConf, SparkContext}

val config = new SparkConf
config.setMaster("local[*]")
config.setAppName("remote scala-spark exec jharte")

val sc = new SparkContext(config)

var nameCategoryFFTDatas = sc.textFile("resources/matprocessedNames.csv").collect()

var nameCategoryFFTData = sc.parallelize(nameCategoryFFTDatas)

println(nameCategoryFFTData.count)