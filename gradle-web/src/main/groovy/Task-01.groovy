import groovy.json.JsonSlurper
import groovy.transform.ToString

@ToString
class Money{
    String name
    BigDecimal amount

    Money plus(Money second){
        def calculatedAmount
        if(second.name == this.name)
            calculatedAmount = second.amount
        else
            calculatedAmount = rate(this.name, second.name)*second.amount

        this.amount += calculatedAmount
        return this
    }

    def rate(String denominator, String nominator){
        def values = new JsonSlurper().parse(new URL("http://api.fixer.io/latest"))
        values.rates[denominator]/values.rates[nominator]
    }

}
println new Money(name: 'USD', amount: 5) + new Money(name: 'CHF', amount:10)


