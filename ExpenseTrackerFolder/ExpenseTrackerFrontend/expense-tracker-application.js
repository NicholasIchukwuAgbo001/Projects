const BACKEND_URL = "http://localhost:4455";
const balanceEl = document.getElementById("balance");
const incomeAmountEl = document.getElementById("income-amount");
const expenseAmountEl = document.getElementById("expense-amount");
const transactionListEl = document.getElementById("transaction-list");
const transactionFormEl = document.getElementById("transaction-form");
const descriptionEl = document.getElementById("description");
const amountEl = document.getElementById("amount");
const loginForm = document.getElementById("login-form");
const signupForm = document.getElementById("signup-form");
const logoutBtn = document.querySelector(".logout-btn");
const searchInput = document.getElementById("search");

let transactions = [];
let currentUser = localStorage.getItem("currentUser");

function showAccessContainer() {
  document.getElementById("access-container").style.display = "flex";
  document.querySelector(".container").style.display = "none";
}

function showAppContainer(userName = "") {
  document.getElementById("access-container").style.display = "none";
  document.querySelector(".container").style.display = "block";

  const welcomeMessageEl = document.getElementById("welcome-message");
  welcomeMessageEl.style.textAlign = "center";
  welcomeMessageEl.style.margin = "1rem 0";
  welcomeMessageEl.style.color = "#d3c7c7";
  welcomeMessageEl.textContent = userName ? `Welcome, ${userName}!` : "";
}

function formatCurrency(number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "NGN",
  }).format(number);
}

function saveCurrentUser(email, name) {
  localStorage.setItem("currentUser", email);
  localStorage.setItem("currentUserName", name);
  currentUser = email;
}

function clearCurrentUser() {
  localStorage.removeItem("currentUser");
  localStorage.removeItem("currentUserName");
  currentUser = null;
}

function validateEmail(email) {
  return /^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email);
}

function validatePasswordLength(password) {
  return password.length >= 4 && password.length <= 16;
}

function createTransactionElement(transaction) {
  const li = document.createElement("li");
  li.classList.add("transaction", transaction.amount > 0 ? "income" : "expense");

  li.style.width = "83%";

  const descSpan = document.createElement("span");
  descSpan.textContent = transaction.description;

  descSpan.style.fontWeight = "600";
  descSpan.style.color = transaction.amount > 0 ? "#4caf50" : "#f44336";

  const amountSpan = document.createElement("span");
  amountSpan.textContent = formatCurrency(transaction.amount);

  amountSpan.style.fontWeight = "600";
  amountSpan.style.color = transaction.amount > 0 ? "#4caf50" : "#f44336";

  const deleteBtn = document.createElement("button");
  deleteBtn.classList.add("delete-btn");
  deleteBtn.textContent = "x";
  deleteBtn.addEventListener("click", () => removeTransaction(transaction.id));

  amountSpan.appendChild(deleteBtn);
  li.appendChild(descSpan);
  li.appendChild(amountSpan);

  return li;
}

function updateTransactionList(filtered = transactions) {
  transactionListEl.innerHTML = "";
  filtered.slice().reverse().forEach(tx => {
    transactionListEl.appendChild(createTransactionElement(tx));
  });
}

function updateSummary(filtered = transactions) {
  const balance = filtered.reduce((acc, t) => acc + t.amount, 0);
  const income = filtered.filter(t => t.amount > 0).reduce((acc, t) => acc + t.amount, 0);
  const expenses = filtered.filter(t => t.amount < 0).reduce((acc, t) => acc + t.amount, 0);

  balanceEl.textContent = formatCurrency(balance);
  incomeAmountEl.textContent = formatCurrency(income);
  expenseAmountEl.textContent = formatCurrency(expenses);
}

async function login() {
  const email = loginForm["login-email"].value.trim();
  const password = loginForm["login-password"].value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  try {
    const res = await fetch(`${BACKEND_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await res.json();

    if (res.ok && data.success) {
      alert(`Welcome ${data.name}`);
      saveCurrentUser(email, data.name);
      showAppContainer(data.name);
      await loadTransactions();
      loginForm.reset();
    } else {
      alert(data.message || "Invalid email or password");
    }
  } catch (err) {
    alert("Login failed: " + err.message);
  }
}

async function signup() {
  const email = signupForm["signup-email"].value.trim();
  const password = signupForm["signup-password"].value.trim();
  const name = signupForm["signup-name"].value.trim();
  const age = parseInt(signupForm["age"].value.trim(), 10);

  if (!email || !password || !name || isNaN(age)) {
    alert("Please fill in all fields correctly.");
    return;
  }

  if (!validateEmail(email)) {
    alert("Please enter a valid Gmail address.");
    return;
  }

  if (!validatePasswordLength(password)) {
    alert("Password must be between 4 and 16 characters long.");
    return;
  }

  if (age <= 0) {
    alert("Please enter a valid age.");
    return;
  }

  try {
    const res = await fetch(`${BACKEND_URL}/api/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, name, age }),
    });

    const data = await res.json();

    if (res.ok && data.success) {
      alert("Signup successful! You can now log in.");
      switchForm("login");
      signupForm.reset();
    } else {
      alert(data.message || "Signup failed");
    }
  } catch (err) {
    alert("Error signing up: " + err.message);
  }
}

async function loadTransactions() {
  if (!currentUser) {
    transactions = [];
    updateTransactionList();
    updateSummary();
    return;
  }

  try {
    const res = await fetch(`${BACKEND_URL}/api/transactions/${currentUser}`);
    const data = await res.json();

    if (res.ok && data.success) {
      transactions = data.transactions || [];
      updateTransactionList();
      updateSummary();
    } else {
      alert(data.message || "Failed to load transactions");
      transactions = [];
      updateTransactionList();
      updateSummary();
    }
  } catch (err) {
    alert("Error loading transactions: " + err.message);
    transactions = [];
    updateTransactionList();
    updateSummary();
  }
}

async function addTransaction(e) {
  e.preventDefault();
  if (!currentUser) {
    alert("Please log in to add transactions.");
    return;
  }

  const description = descriptionEl.value.trim();
  const amount = parseFloat(amountEl.value);

  if (!description || isNaN(amount) || amount === 0) {
    alert("Please enter a valid description and amount.");
    return;
  }

  try {
    const res = await fetch(`${BACKEND_URL}/api/transactions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId: currentUser, description, amount }),
    });

    const data = await res.json();

    if (res.ok && data.success) {
      alert("Transaction added successfully");
      await loadTransactions();
      transactionFormEl.reset();
    } else {
      alert(data.message || "Failed to add transaction");
    }
  } catch (err) {
    alert("Error adding transaction: " + err.message);
  }
}

async function removeTransaction(id) {
  try {
    const res = await fetch(`${BACKEND_URL}/api/transactions/${id}`, { method: "DELETE" });
    const data = await res.json();

    if (res.ok && data.success) {
      alert("Transaction removed");
      await loadTransactions();
    } else {
      alert(data.message || "Failed to remove transaction");
    }
  } catch (err) {
    alert("Error removing transaction: " + err.message);
  }
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

searchInput.addEventListener("input", () => {
  const query = searchInput.value.toLowerCase();
  const filtered = transactions.filter(tx =>
    tx.description.toLowerCase().includes(query)
  );
  updateTransactionList(filtered);
  updateSummary(filtered);
});

logoutBtn.addEventListener("click", () => {
  clearCurrentUser();
  transactions = [];
  showAccessContainer();
  updateTransactionList();
  updateSummary();
});

loginForm.addEventListener("submit", e => {
  e.preventDefault();
  login();
});

signupForm.addEventListener("submit", e => {
  e.preventDefault();
  signup();
});

transactionFormEl.addEventListener("submit", addTransaction);

if (currentUser) {
  const userName = localStorage.getItem("currentUserName") || "";
  showAppContainer(userName);
  loadTransactions();
} else {
  showAccessContainer();
}
