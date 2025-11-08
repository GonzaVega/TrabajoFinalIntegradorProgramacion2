// EntradaInvalidaException.java
package com.utils;

public class EntradaInvalidaException extends Exception {
  private String entradaRecibida;
  private String formatoEsperado;
    
  public EntradaInvalidaException(String mensaje) {
    super(mensaje);
   }
    
  public EntradaInvalidaException(String mensaje, Throwable causa) {
    super(mensaje, causa);
   }
    
  public EntradaInvalidaException(String mensaje, String entradaRecibida, String formatoEsperado) {
    super(mensaje);
    this.entradaRecibida = entradaRecibida;
    this.formatoEsperado = formatoEsperado;
   }
    
  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder(super.getMessage());
    if (entradaRecibida != null && !entradaRecibida.isEmpty()) {
        sb.append("\n  → Entrada recibida: '").append(entradaRecibida).append("'");
     }
    if (formatoEsperado != null) {
        sb.append("\n  → Formato esperado: ").append(formatoEsperado);
     }
     return sb.toString();
    }
    
  public String getEntradaRecibida() {
     return entradaRecibida;
    }
    
  public String getFormatoEsperado() {
     return formatoEsperado;
    }
}
