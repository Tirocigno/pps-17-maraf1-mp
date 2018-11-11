# **pps-17-maraf1-md**
Elaborato di progetto per l'esame di PPS, anno 2017-18

## Team members
 * Jacopo Riciputi: [jacopo.riciputi@studtio.unibo.it](mailto:jacopo.riciputi@studio.unibo.it)
 
 * Nicholas Brasini: [nicholas.brasini@studio.unibo.it](mailto:nicholas.brasini@studio.unibo.it)
 
 * Federico Naldini: [federico.naldini3@studio.unibo.it](mailto:federico.naldini3@studio.unibo.it)
 
 * Gjulio Jakova: [gjulio.jakova@studio.unibo.it](mailto:gjulio.jakova@studio.unibo.it)
 
 ## Guida all'utilizzo
 Le singole funzionalità del sistema sono avviabili da Gradle rispettivamente dai tasks runClient, runServer
 e runDiscovery, a cui gli argomenti cli possono essere passati tramite il flag --args='--argumentName=argumentValue'.
 Per lanciare i tre jar, può essere specificata o meno una configurazione da CLI.
 Di default, il discovery è configurato per essere in esecuzione su 127.0.0.1:2000, i vari server su 127.0.0.1:4700
 mentre i client ricercano nodi di Akka Cluster e discovery all'indirizzo 127.0.0.1.
 Se si desidera lanciare il sistema in remoto, è necessario configurare i seguenti parametri all' avvio:
 
 *Discovery*
 * --myip: specifica l'indirizzo di rete dell'host su cui verrà messo in esecuzione il discovery.
 * --myport: specifica la porta di rete dell'host su cui verrà messo in esecuzione il discovery.
 
  *Client*
  * --discoveryip:  specifica l'indirizzo di rete su cui è in esecuzione il discovery.
  * --discoveryport:  specifica la porta di rete su cui è in esecuzione il discovery.
  * --currentip: specifica l'indirizzo di rete dell'host su cui verrà messo in esecuzione il client.
  
  *Server*
  * --discoveryaddress:  specifica l'indirizzo di rete su cui è in esecuzione il discovery.
  * --myip:  specifica l'indirizzo di rete su cui è in esecuzione il server.
  * --redishost:  specifica l'host su cui è in esecuzione il server di Redis
  * --redisport:  specifica la porta su cui è in esecuzione il server di Redis 
  * --redispw:  specifica la password per accedere al server di Redis
 
 
