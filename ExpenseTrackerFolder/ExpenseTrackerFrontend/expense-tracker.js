const balanceEl = document.getElementById("balance");
const incomeAmountEl = document.getElementById("income-amount");
const expenseAmountEl = document.getElementById("expense-amount");
const transactionListEl = document.getElementById("transaction-list");
const transactionFormEl = document.getElementById("transaction-form");
const descriptionEl = document.getElementById("description");
const amountEl = document.getElementById("amount");
const loginForm = document.getElementById("login-form");
const signupForm = document.getElementById("signup-form");
const loginBtn = document.getElementById("login-btn");
const signupBtn = document.getElementById("signup-btn");
const logoutBtn = document.querySelector(".logout-btn");

let transactions = [];
let currentUser = localStorage.getItem("currentUser");

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("currentUser");
  currentUser = null;
  transactions = [];
  document.getElementById("access-container").style.display = "block";
  document.querySelector(".container").style.display = "none";
  updateTransactionList();
  updateSummary();
});

if (currentUser) {
  document.getElementById("access-container").style.display = "none";
  document.querySelector(".container").style.display = "block";
  loadTransactions();
  updateTransactionList();
  updateSummary();
}

function switchForm(name) {
  if (name === "login") {
    loginForm.classList.add("active");
    signupForm.classList.remove("active");
  } else if (name === "signup") {
    signupForm.classList.add("active");
    loginForm.classList.remove("active");
  }
}

function login() {
  const email = document.getElementById("login-email").value.trim();
  const password = document.getElementById("login-password").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  const savedUsers = JSON.parse(localStorage.getItem("users")) || [];
  const user = savedUsers.find((u) => u.email === email && u.password === password);

  if (user) {
    alert(`Welcome ${user.name}`);
    currentUser = email;
    localStorage.setItem("currentUser", currentUser);
    document.getElementById("access-container").style.display = "none";
    document.querySelector(".container").style.display = "block";
    loadTransactions();
    updateTransactionList();
    updateSummary();
    loginForm.reset();
  } else {
    alert("Invalid email or password");
  }
}

function signup() {
  const email = document.getElementById("signup-email").value.trim();
  const password = document.getElementById("signup-password").value.trim();
  const name = document.getElementById("signup-name").value.trim();

  if (!email || !password || !name) {
    alert("Please fill in all fields.");
    return;
  }

  let savedUsers = JSON.parse(localStorage.getItem("users")) || [];

  if (savedUsers.some((user) => user.email === email)) {
    alert("User already exists with that email");
    return;
  }

  savedUsers.push({ email, password, name });
  localStorage.setItem("users", JSON.stringify(savedUsers));
  alert("Signup successful! You can now log in.");
  switchForm("login");
  signupForm.reset();
}

function loadTransactions() {
  if (!currentUser) {
    transactions = [];
  } else {
    transactions = JSON.parse(localStorage.getItem(`transactions_${currentUser}`)) || [];
  }
}

function saveTransactions() {
  if (currentUser) {
    localStorage.setItem(`transactions_${currentUser}`, JSON.stringify(transactions));
  }
}

function addTransaction(e) {
  e.preventDefault();
  if (!currentUser) {
    alert("Please log in to add transactions.");
    return;
  }

  const description = descriptionEl.value.trim();
  const amount = parseFloat(amountEl.value);

  if (!description || isNaN(amount)) {
    alert("Please enter a valid description and amount.");
    return;
  }

  const id = crypto.randomUUID(); // Better than Date.now() for uniqueness
  transactions.push({ id, description, amount });
  saveTransactions();
  updateTransactionList();
  updateSummary();
  transactionFormEl.reset();
}

function removeTransaction(id) {
  transactions = transactions.filter((transaction) => transaction.id !== id);
  saveTransactions();
  updateTransactionList();
  updateSummary();
}

function updateTransactionList() {
  transactionListEl.innerHTML = "";
  const sortedTransactions = [...transactions].reverse();
  sortedTransactions.forEach((transaction) => {
    const transactionEl = createTransactionElement(transaction);
    transactionListEl.appendChild(transactionEl);
  });
}

function createTransactionElement(transaction) {
  const liTag = document.createElement("li");
  liTag.classList.add("transaction");
  liTag.classList.add(transaction.amount > 0 ? "income" : "expense");
  liTag.innerHTML = `
    <span>${transaction.description}</span>
    <span>
      ${formatCurrency(transaction.amount)}
      <button class="delete-btn" onclick="removeTransaction('${transaction.id}')">x</button>
    </span>
  `;
  return liTag;
}

function updateSummary() {
  const balance = transactions.reduce((acc, transaction) => acc + transaction.amount, 0);
  const income = transactions
    .filter((transaction) => transaction.amount > 0)
    .reduce((acc, transaction) => acc + transaction.amount, 0);
  const expenses = transactions
    .filter((transaction) => transaction.amount < 0)
    .reduce((acc, transaction) => acc + transaction.amount, 0);
  balanceEl.textContent = formatCurrency(balance);
  incomeAmountEl.textContent = formatCurrency(income);
  expenseAmountEl.textContent = formatCurrency(expenses);
}

function formatCurrency(number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "NGN",
  }).format(number);
}

loginBtn.addEventListener("click", (e) => {
  e.preventDefault();
  login();
});

signupBtn.addEventListener("click", (e) => {
  e.preventDefault();
  signup();
});

transactionFormEl.addEventListener("submit", addTransaction);

loadTransactions();
updateTransactionList();
updateSummary();
