FROM rabbitmq:3-management
COPY plugins/rabbitmq_message_timestamp-3.10.0.ez /plugins/
RUN rabbitmq-plugins enable --offline rabbitmq_message_timestamp

#To build an image
 #docker build -t rabbitmq-with-timestamp .