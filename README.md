# integration-casting-issue

# rabbitmq container 
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# publishing & consuming a message 

Use rabbitmq management page for message publishing
http://localhost:15672/#/queues/%2F/orderQueue.OrderRequest

Sample OrderMessage for publishing:

{"orderId":16660,"protocolId":88}

![image](https://github.com/user-attachments/assets/df4dea0e-68ef-49a8-935b-81627ef229d6)


processOrderRequestFlow consumes the message

# Object casting issue
I could consume the message directly as OrderMessage before I upgraded spring-integration and spring-cloud-stream versions

Before 

![old_version_object_casting](https://github.com/user-attachments/assets/fa986b49-0e2f-4b69-aed1-13464a62014e)

After upgradation, I can consume the same message as byte[]. Otherwise I get ClassCastExeption

After

![new_version_casting_issue](https://github.com/user-attachments/assets/c31f2939-dce1-4b98-a57c-53b220b22546)


