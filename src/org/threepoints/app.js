// Ejemplo simple de funciones en JavaScript para SonarQube
function sumar(a, b) {
  return a + b;
}

function restar(a, b) {
  return a - b;
}

function multiplicar(a, b) {
  return a * b;
}

function dividir(a, b) {
  if (b === 0) {
    throw new Error("No se puede dividir por cero");
  }
  return a / b;
}

// Llamadas de ejemplo
console.log(sumar(4, 5)); // 9
console.log(restar(10, 5)); // 5
console.log(multiplicar(3, 3)); // 9
console.log(dividir(10, 2)); // 5
