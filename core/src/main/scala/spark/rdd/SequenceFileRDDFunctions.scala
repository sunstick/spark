package spark.rdd

import org.apache.hadoop.io.Writable
import org.apache.hadoop.mapred.SequenceFileOutputFormat
import spark.Logging

/**
 * Extra functions available on RDDs of (key, value) pairs to create a Hadoop SequenceFile,
 * through an implicit conversion. Note that this can't be part of PairRDDFunctions because
 * we need more implicit parameters to convert our keys and values to Writable.
 */
class SequenceFileRDDFunctions[K <% Writable: ClassManifest, V <% Writable : ClassManifest](
    self: RDD[(K,V)])
  extends Logging
  with Serializable {
  
  def getWritableClass[T <% Writable: ClassManifest](): Class[_ <: Writable] = {
    val c = {
      if (classOf[Writable].isAssignableFrom(classManifest[T].erasure)) { 
        classManifest[T].erasure
      } else {
        implicitly[T => Writable].getClass.getMethods()(0).getReturnType
      }
       // TODO: use something like WritableConverter to avoid reflection
    }
    c.asInstanceOf[Class[ _ <: Writable]]
  }

  def saveAsSequenceFile(path: String) {
    def anyToWritable[U <% Writable](u: U): Writable = u

    val keyClass = getWritableClass[K]
    val valueClass = getWritableClass[V]
    val convertKey = !classOf[Writable].isAssignableFrom(self.getKeyClass)
    val convertValue = !classOf[Writable].isAssignableFrom(self.getValueClass)
  
    logInfo("Saving as sequence file of type (" + keyClass.getSimpleName + "," + valueClass.getSimpleName + ")" ) 
    val format = classOf[SequenceFileOutputFormat[Writable, Writable]]
    if (!convertKey && !convertValue) {
      self.saveAsHadoopFile(path, keyClass, valueClass, format) 
    } else if (!convertKey && convertValue) {
      self.map(x => (x._1,anyToWritable(x._2))).saveAsHadoopFile(path, keyClass, valueClass, format) 
    } else if (convertKey && !convertValue) {
      self.map(x => (anyToWritable(x._1),x._2)).saveAsHadoopFile(path, keyClass, valueClass, format) 
    } else if (convertKey && convertValue) {
      self.map(x => (anyToWritable(x._1),anyToWritable(x._2))).saveAsHadoopFile(path, keyClass, valueClass, format) 
    } 
  }
}
