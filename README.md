# Payment Manager

REST API para emissão de boletos e controle de arquivos CNAB 240

## Stack

#### Java 8
#### Spring Boot
    Framework para API Rest
#### Spring Security
    Controle de acesso, autenticação e autorização de usuários
#### Keycloak
    Ferramenta open-source para gerenciamento de pessoas e acesso
#### Spring Data JPA
    Simplifica a implementação do JPA e ORMs
#### Hibernate 
    ORM para gerenciamento e persistência de dados no banco
#### Flyway 
    Versionamento de banco de dados
#### Retrofit 
    Biblioteca para requisições HTTP
#### Stella 
    Criação de boletos
#### Jasper Report 
    Geração de boletos em PDF
#### Swagger 
    Documentação da API

## Biblioteca própria CNAB 240

A biblioteca foi desenvolvida para simplificar a criação e leitura de arquivos CNAB 240, utilizando templates no formato YAML. No código atual, foi implementado o template para o banco Santander.

### O que é CNAB 240?
CNAB é uma abreviação para Centro Nacional de Automação Bancária, isto é, uma interface para troca de informações entre o emitente de boleto e o banco.

Ele determina o formato de texto e a quantidade de colunas presentes nos arquivos de remessa e nos arquivos de retorno, a fim de facilitar sua leitura.


## Autor

Bruno Silveira 


[www.linkedin.com/in/bbrazsilveira/](https://www.linkedin.com/in/bbrazsilveira/)