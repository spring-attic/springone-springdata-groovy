package org.springframework.data.groovy

import com.mongodb.Mongo
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

/**
 * @author Jon Brisbin <jon@jbrisbin.com>
 */
class MongoBuilder {

  static {
    ExpandoMetaClass.enableGlobally()
  }

  MongoTemplate template = new MongoTemplate(new Mongo("127.0.0.1"), "springone")

  def propertyMissing(String name) {
    println "property missing: $name"
    def coll = new MongoCollection(template: template, name: name)
    MongoBuilder.metaClass."$name" = coll
    coll
  }

  def methodMissing(String name, obj) {
    println "method missing: $name, $obj"
    Object[] args = obj
    Closure cl = args[0]
    def coll = new MongoCollection(template: template, name: name)
    cl.setDelegate(coll)
    cl.call()
    MongoBuilder.metaClass."$name" = coll
  }

}

class MongoCollection {
  MongoTemplate template
  String name

  def count() {
    template.getCollection(name).count()
  }

  def drop() {
    template.dropCollection(name)
    this
  }

  def insert(Object... objs) {
    objs.each {
      template.insert(it, name)
    }
  }

  def leftShift(Object... objs) {
    insert(objs)
  }

  def select(Closure cl) {
    def query = new MongoQuery(collection: this, template: template)
    cl.setDelegate(query)
    cl.call()
    query
  }
}

class MongoQuery {
  MongoCollection collection
  MongoTemplate template
  Query query = new Query()
  Criteria criteria

  def where(String name) {
    criteria = Criteria.where(name)
    query.addCriteria(criteria)
    this
  }

  def eq(obj) {
    criteria.is(obj)
  }

  def collect(Closure cl) {
    def targetClass = cl.getParameterTypes()[0]
    def results = template.find(query, targetClass, collection.name)
    results.collect {
      cl(it)
    }
  }
}
