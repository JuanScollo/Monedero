package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cuanto) {
    validarMontoPostivo(cuanto);
    validarLimitesDeposicionDiario();
    registrarDeposito(cuanto);
  }

  public void sacar(double cuanto) {
    // se usa el metodo abstraido
    validarMontoPostivo(cuanto);
    validarSaldoSuficiente(cuanto);
    validarLimiteExtraccionDiaria(cuanto);
    registrarExtraccion(cuanto);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    var movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .mapToDouble(movimiento -> movimiento.montoDeExtraccionDe(fecha))
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }


  // Se saco la logica de validar montos positvos del metodo poner
  private void validarMontoPostivo(double cuanto){
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  // Se saco la logica de validar los limites de deposicion de poner
  private void validarLimitesDeposicionDiario() {
    if (getMovimientos().stream()
        .filter(movimiento -> movimiento.fueDepositado(LocalDate.now()))
        .count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  // Se saco la logica de crear un nuevo movimiento
  private void registrarDeposito(double cuanto) {
    new Movimiento(LocalDate.now(), cuanto, true).agregateA(this);
  }

  // Se saco la logica de validarSaldoSuficiente
  private void validarSaldoSuficiente(double cuanto) {
    if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  // Se saco la logica de validar el limite de extraccion diaria
  private void validarLimiteExtraccionDiaria(double cuanto) {
    var montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    var limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException(
          "No puede extraer mas de $ " + 1000 + " diarios, " + "límite: " + limite);
    }
  }

  // Se abstrajo la logica de extraccion
  private void registrarExtraccion(double cuanto) {
    new Movimiento(LocalDate.now(), cuanto, false).agregateA(this);
  }
}
