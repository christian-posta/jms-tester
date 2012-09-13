

#JMSTester

As you are reading this introduction you have probably downloaded the JMS Test Framework. Why have you done that? There might be a number of reasons:

* You want to learn about JMS and went searching for some sample code that does more than sending and receiving a “Hello World” message.
* You want to benchmark your particular JMS Provider and the usual benchmarking suspects seemed to complicated or haven’t done the job for you.
* You want to fine tune the performance of your JMS layer specifically to the throughput you have to support and make sure you have selected a scalable architecture.

In all cases you have come to the right place. The JMS Test Framework has grown over several years – starting it’s life on a JMS consultant’s laptop as a single stand alone JMS application that would send as many messages to a JMS broker as possible and also consume them as fast as possible with some minor statistics like msg/s produced, msg/s consumed etc.

## Wasn't this a fusesource project?
Yes, it was hosted at the fusesource forge. However, with Red Hat buying FuseSource recently, I wanted to make sure this project lived on and didn't get swept away in the migration effort.

