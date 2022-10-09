 new File('my-ip-address.txt').withWriter('utf-8') { 
         writer -> writer.writeLine new URL("https://api.ipify.org").text 
      } 


