package br.com.rcc_dev.testes.entities;

import br.com.rcc_dev.testes.entities.database.Cep;
import lombok.Data;

@Data
public class CepOutput extends Cep {
  
  protected boolean database;
  
  public CepOutput(){}
  public CepOutput(Cep cep){
    this.setId( cep.getId() );
    this.setBairro( cep.getBairro() );
    this.setCep( cep.getCep() );
    this.setCidade( cep.getCidade() );
    this.setComplemento( cep.getComplemento() );
    this.setComplemento2( cep.getComplemento2() );
    this.setEndereco( cep.getEndereco() );
    this.setUf( cep.getUf() );
  }
  

}
