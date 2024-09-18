// example.js

/**
 * Suma dos números.
 * @param {number} a - El primer número.
 * @param {number} b - El segundo número.
 * @returns {number} La suma de los dos números.
 */
function suma(a, b) {
  return a + b;
}

/**
 * Resta dos números.
 * @param {number} a - El primer número.
 * @param {number} b - El segundo número.
 * @returns {number} La resta de los dos números.
 */
function resta(a, b) {
  return a - b;
}

/**
 * Multiplica dos números.
 * @param {number} a - El primer número.
 * @param {number} b - El segundo número.
 * @returns {number} El producto de los dos números.
 */
function multiplica(a, b) {
  return a * b;
}

/**
 * Divide dos números.
 * @param {number} a - El primer número.
 * @param {number} b - El segundo número.
 * @returns {number} El cociente de los dos números.
 */
function divide(a, b) {
  if (b === 0) {
    throw new Error("No se puede dividir por cero");
  }
  return a / b;
}

// Ejemplo de uso
console.log(suma(2, 3)); // 5
console.log(resta(5, 2)); // 3
console.log(multiplica(4, 3)); // 12
console.log(divide(10, 2)); // 5
