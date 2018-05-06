create table cep (
  id                            integer auto_increment not null,
  bairro                        varchar(255) not null,
  cep                           varchar(255) not null,
  cidade                        varchar(255) not null,
  complemento                   varchar(255) not null,
  complemento2                  varchar(255) not null,
  endereco                      varchar(255) not null,
  uf                            varchar(255) not null,
  constraint pk_cep primary key (id)
);

