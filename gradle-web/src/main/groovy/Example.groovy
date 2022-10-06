import groovy.json.JsonSlurper
import groovy.transform.Canonical

println("--- multi assigning ")
def pairFunction(){
    return ['one', 1]
}
(a,b) = pairFunction()
println (a + "   " + b )

println("--- casting")
class Person{
    String name
    int age

    @Override
    String toString() {
        // GString - Groovy String
        "Instance of Person: name -->$name<-- and age -->${age}<--"
    }

    static List<Person> filter(List<Person> list, Closure<Person> evaluator){
        List<Person> returnValue = []
        Iterator<Person> iterator = list.iterator()
        while(iterator.hasNext()){
            Person currentValue = iterator.next();
            if(evaluator.call(currentValue)){
                returnValue << currentValue
            }
        }
        return returnValue
    }

}

def dataLikePerson(){
    [name:'Vasya', age:20]
}
println dataLikePerson().getClass()
println dataLikePerson() as Person

println("--- avoid NPE during evaluation")
Person newPerson = null
println newPerson?.name?.length()
if(newPerson?.age < -10) println newPerson?.age + " - " + "young boy"


println("--- constructors in Groovy")
println new Person(age:200, name: 'SuperUser')

println("--- parsing example")
def moneyData = new JsonSlurper().parseText(new URL("http://api.fixer.io/latest").text)
println "Base: $moneyData.base, one certain rate: ${moneyData.rates["CHF"]}"

println("--- List")
def numbers = [1,2,3,4]
numbers[8-1] = 8
numbers<<9
numbers<<10<<11
println numbers
println numbers-[1,2,3,10,11]
println numbers[0..3,7..10]
for(each_number in numbers) print "$each_number, "; println()
println "${numbers[100]} - out of range value, not OutOfBoundException"

println("--- ranges")
def range1 = 1..<10
println range1
println range1.getClass()
println "example of contains method: 5 = ${range1.contains(5)}  5.0 = ${range1.contains(5.0)}"
String filename = "my_personal_file.gif"
println filename[0..<-4] // filename[0..-5]

println("--- switch")
def determinator(value){
    switch(value){
        case Person: println "this is person: $value"; break
        case Money: println "this is money $value"; break
        case 2..4: break
        case 10..Integer.MAX_VALUE : println "more than 10"; break
        default: "unknown"
    }
}
determinator(new Person(age:10, name: 'Vanya'))
determinator(new Money(name: 'CHF', amount: 10**8))
determinator(20)

println("--- map")
def map = [value1 : 10, value2 : 20] // key is considered like string

println "${map["value1"]}  ${map.get("value1")}   ${map["value2"]}  ${map.value2} "


println("--- closure")

def closure1 = {println "hello closure"}
closure1()
closure1.call()
def closureWithParameter = {inputData -> println inputData}
closureWithParameter("closure output")

println("--- methods of list")
List<Person> persons = [new Person([name:"V", age: 10]), new Person(name:"A", age:7), new Person(name: "C", age:9)]
println(" sort persons by age ${persons.sort(false, {x,y->x.age <=> y.age})}" )
println("names of the collection ${persons.collect({each->each.name})}")
println("names of the collection ${persons.collect({it.name})}")
println("names of the collection ${persons*.name}")

println(Person.filter(persons, {p -> Math.abs(10 - p.age)<3}))

println("--- metaclass of Class")
Person.metaClass.defaultAge = 10
Person.metaClass.nameLength = {delegate.name?.length()}
Person.metaClass.nameLength << {delegate.name?.length()}
Person.metaClass.propertyMissing = {name-> "-------"}

println("property of metaClass 'defaultAge' " + new Person().getDefaultAge())
println("property of metaClass 'nameLength' " + new Person(name:"Vanya", age:10).nameLength())
println("--- metaclass of Object")
def person1 = new Person(name:"Vanya", age:10)
person1.metaClass.defaultName = "Unknown"
def person2 = new Person([name:"Kolya", age:12])


println("instance with property: " + person1.defaultName)
try{
    println("instance without property: " + person2.defaultName )
}catch(ex){
}
