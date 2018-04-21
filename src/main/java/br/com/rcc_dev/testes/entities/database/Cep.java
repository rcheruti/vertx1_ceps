package br.com.rcc_dev.testes.entities.database;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "cep")
public class Cep {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected int id;
  
  @Column(name = "bairro")
  protected String bairro;
  
  @Column(name = "cep")
  protected String cep;
  
  @Column(name = "cidade")
  protected String cidade;
  
  @Column(name = "complemento")
  protected String complemento;
  
  @Column(name = "complemento2")
  protected String complemento2;
  
  @Column(name = "endereco")
  protected String endereco;
  
  @Column(name = "uf")
  protected String uf;
  
}
