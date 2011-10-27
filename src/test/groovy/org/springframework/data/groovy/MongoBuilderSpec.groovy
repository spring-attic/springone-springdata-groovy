package org.springframework.data.groovy

import org.springframework.data.annotation.Id
import spock.lang.Specification

class MongoBuilderSpec extends Specification {

  def "Test MongoBuilder"() {

    given:
    def db = new MongoBuilder()

    when:
    def coll = db.springone.drop()
    coll << [
        new Person(firstName: "Jon", lastName: "Brisbin"),
        new Person(firstName: "Jesse", lastName: "James"),
        new Person(firstName: "Frank", lastName: "James")
    ]
    def query = coll.sendPosseAfter {
      where "lastName" eq "James"
    }
    def results = query.collect {Person p ->
      p.id
    }
    println "ids: $results"

    then:
    true

  }
}

class Person {
  @Id
  String id
  String firstName
  String lastName

  String toString() {
    "Person(id=$id, firstName=$firstName, lastName=$lastName)"
  }
}