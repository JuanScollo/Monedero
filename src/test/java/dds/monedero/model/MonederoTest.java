package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  @DisplayName("Es posible poner $1500 en una cuenta vacía")
  void Poner() {
    cuenta.poner(1500);
  }

  @Test
  @DisplayName("No es posible poner montos negativos")
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  @DisplayName("Es posible realizar múltiples depósitos consecutivos")
  void TresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
  }

  @Test
  @DisplayName("No es posible superar la máxima cantidad de depositos diarios")
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
      for (int i = 0; i < Cuenta.MAX_DEPOSITOS_DIARIOS + 1; i++) {
        cuenta.poner(100); // Monto cualquiera
      }
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el saldo disponible")
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
      cuenta.setSaldo(90);
      cuenta.sacar(1001);
    });
  }

  @Test
  @DisplayName("No es posible extraer más que el límite diario")
  void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(Cuenta.LIMITE_EXTRACCION_DIARIA + 1);
    });
  }

  @Test
  @DisplayName("No es posible extraer un monto negativo")
  void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  @DisplayName("No es posible extraer con saldo cero")
  void ExtraerConSaldoCero() {
    assertThrows(SaldoMenorException.class, () -> cuenta.sacar(100));
  }

  @Test
  @DisplayName("Un depósito registra un movimiento correctamente")
  void MovimientoDepositoRegistrado() {
    cuenta.poner(1000);
    assertEquals(1, cuenta.getMovimientos().size());
    assertTrue(cuenta.getMovimientos().get(0).isDeposito());
  }


  @Test
  @DisplayName("No se puede superar el límite diario entre extracciones")
  void ExtraccionesAcumulativasSuperanLimiteDiario() {
    cuenta.poner(2000);
    cuenta.sacar(500);
    cuenta.sacar(500);
    assertThrows(MaximoExtraccionDiarioException.class, () -> cuenta.sacar(1));
  }

}