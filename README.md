# ADS1242 - Atividade 02: Notificações com Kafka

<div align="center">

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)

</div>

## Instruções
<p align="justify"> Desenvolva um sistema de gerenciamento de eventos e notificações utilizando Kafka como base para a comunicação entre os serviços. O sistema deverá ser capaz de lidar com a geração, processamento e envio de notificações em tempo real e em lote. Imagine que o sistema será utilizado em um ambiente acadêmico para gerenciar notificações de eventos, como lembretes de tarefas, avisos de alterações no cronograma e eventos importantes, como palestras ou workshops. </p>

## Servidor

> - <p align="justify"> As notificações deverão ser enviadas tanto individualmente quanto em lotes, dependendo do volume de eventos que precisam ser processados e enviados aos usuários. </p>
> - <p align="justify"> Implemente um produtor Kafka responsável por gerar e publicar eventos de notificação para um ou mais tópicos. Estes eventos podem variar, como avisos de última hora, lembretes de prazo de entrega de trabalhos ou mudanças de horário de aulas. Utilize o Kafka para permitir que o produtor envie notificações em tempo real, mas também agrupe eventos em lotes quando necessário para melhorar o desempenho do sistema. </p>
> - <p align="justify"> Por outro lado, crie um consumidor Kafka que seja capaz de processar as notificações recebidas e enviar para os usuários simulados. O consumidor deve ser implementado usando a anotação <strong>@KafkaListener</strong>, com configuração para o processamento em lotes. </p>
> - <p align="justify"> O produtor será responsável por decidir se uma notificação deve ser enviada imediatamente ou se pode ser agrupada com outras notificações e enviada posteriormente. Implemente um sistema de prioridade para os eventos, onde as notificações de alta prioridade são enviadas imediatamente e as de baixa prioridade podem ser enviadas em lote. O sistema deve ser flexível para lidar com diferentes tipos de eventos e prioridades. Utilize um sistema de logs eficiente para registrar todas as ações realizadas pelo consumidor e produtor Kafka. </p>

## Cliente

> - <p align="justify"> A parte visual pode ser simples, mas deve exibir de forma clara as notificações que estão sendo enviadas e recebidas. Simule o envio de notificações para diferentes tipos de usuários, como alunos, professores e administradores, cada um com um tipo de prioridade de evento. </p>
