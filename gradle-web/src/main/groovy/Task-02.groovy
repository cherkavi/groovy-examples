def listOfPerson = [new Person([name:"Vasya", age:10]), new Person([name:"Kolya", age:20]) ]
listOfPerson.each{it.age+=10}
println listOfPerson