import org.apache.camel.builder.RouteBuilder;

// import org.apache.camel.cdi.Main;
import org.apache.camel.main.Main;

public class TimerRouteBuilder extends RouteBuilder {
	
    @Override
    public void configure() throws Exception {

      from('timer:tick?period=3000')
        .setBody().constant('Hello world from Camel K')
        .to('log:info')

    }

}

println "start"
Main main = new Main();
main.addRouteBuilder(new TimerRouteBuilder());
main.run([]);
main.close();

println "finish"
