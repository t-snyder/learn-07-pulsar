package learn.pulsar;


import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarClientMain
{
  private static final boolean tls           = true;
  private static final String  serviceUrl    = "pulsar://192.168.49.20:6650";
  private static final String  tlsServiceUrl = "pulsar+ssl://192.168.49.20:6651";
  private static final String  certPath      = "/media/tim/ExtraDrive1/Projects/learn-07-pulsar/pulsar-client/src/main/resources/tls.crt";
  private PulsarClient         client        = null;
  private Producer<byte[]>     producer      = null;
  private Consumer<byte[]>     consumer      = null;

  private int numMsgs   = 10;
  
  public PulsarClientMain()
  {
    try
    {
      if( tls ) { buildTlsClient(); }
      else {      buildClient();    }
    } 
    catch( PulsarClientException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    
    System.out.println( "PulsarClient initiated.");
  }

  public void createTestConsumer()
  {
    MessageListener<byte[]> myMessageListener = (consumer, msg) -> {
      try 
      {
        consumer.acknowledge(msg);
        System.out.println( "Consumer - Message Received and Ack'd - " + new String( msg.getData() ));
      } 
      catch( Exception e ) 
      {
        consumer.negativeAcknowledge(msg);
        System.out.println( "Consumer exception - " +e.getMessage() );
        return;
      }
    };

    try
    {
      consumer = client.newConsumer()
          .topic("my-topic")
          .subscriptionName( "my-subscription" )
          .messageListener( myMessageListener )
          .subscribe();
    } 
    catch( PulsarClientException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }    

  }

  public void createTestProducer()
  {
    try
    {
      producer = client.newProducer().topic( "my-topic" ).create();
    } catch( PulsarClientException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // You can then send messages to the broker and topic you specified:
    try
    {
      for( int i = 0; i < numMsgs; i++ )
      {
        String msg = "A message with id = " + i;
        producer.send( msg.getBytes() );
        
        System.out.println( "Producer message sent - " + msg );
      }
    } 
    catch( PulsarClientException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
  }

  /**
   * Note - The prog will generally close prior to the consumer being able to read the final 
   * msg. As the real purpose of this test is about connecting with the kubernetes deployed
   * Pulsar nothing was done about it.
   */
  public void closePulsarClient()
  {
    try
    {
      producer.close();
      consumer.close();
      client.close();   
    } 
    catch( PulsarClientException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    
    System.out.println( "Pulsar producer, consumer and client closed." );
  }
  
  private void buildClient() 
   throws PulsarClientException
  {
    client = PulsarClient.builder().serviceUrl( serviceUrl ).build();
  }
  
  private void buildTlsClient() 
   throws PulsarClientException
  {
    client = PulsarClient.builder()
      .serviceUrl( tlsServiceUrl )
      .tlsTrustCertsFilePath( certPath )
      .enableTlsHostnameVerification( false ) // false by default, in any case
      .allowTlsInsecureConnection( false ) // false by default, in any case
      .build();    
  }
  public static void main( String[] args )
  {
    PulsarClientMain clientMain = new PulsarClientMain();
    
    clientMain.createTestConsumer();
    clientMain.createTestProducer();
    
    clientMain.closePulsarClient();
  }

}
