package br.com.rcc_dev.testes.entities

import javax.persistence.*

@Entity
@Table(name = "cep")
data class Cep(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Int = 0 ,

  @Column(name = "bairro")
  var bairro: String = "" ,

  @Column(name = "cep")
  var cep: String = "" ,

  @Column(name = "cidade")
  var cidade: String = "" ,

  @Column(name = "complemento")
  var complemento: String = "" ,

  @Column(name = "complemento2")
  var complemento2: String = "" ,

  @Column(name = "endereco")
  var endereco: String = "" ,

  @Column(name = "uf")
  var uf: String = "" ,

  // --------------------

  @Transient
  var database: Boolean = false

){ }