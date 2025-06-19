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

function showMessage(message, type = "success") {
  const container = document.getElementById("message-container");
  const msgEl = document.createElement("div");
  msgEl.classList.add("message", type);
  msgEl.textContent = message;
  container.appendChild(msgEl);

  setTimeout(() => {
    msgEl.remove();
  }, 5000); 
}

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
  return !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/.test(email);
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
    showMessage("Please enter both email and password.", "error");
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
      showMessage(`Welcome ${data.name}`, "success");
      saveCurrentUser(email, data.name);
      showAppContainer(data.name);
      await loadTransactions();
      loginForm.reset();
    } else {
      showMessage(data.message || "Invalid email or password", "error");
    }
  } catch (err) {
    showMessage("Login failed: " + err.message, "error");
  }
}


async function signup() {
  const email = signupForm["signup-email"].value.trim();
  const password = signupForm["signup-password"].value.trim();
  const name = signupForm["signup-name"].value.trim();
  const age = parseInt(signupForm["age"].value.trim(), 10);

  if (!email || !password || !name || isNaN(age)) {
    showMessage("Please fill in all fields correctly.", "error");
    return;
  }

  if (!validateEmail(email)) {
    showMessage("Please enter a valid Gmail address.", "error");
    return;
  }

  if (!validatePasswordLength(password)) {
    showMessage("Password must be between 4 and 16 characters long.", "error");
    return;
  }

  if (age <= 0) {
    showMessage("Please enter a valid age.", "error");
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
      showMessage("Signup successful! You can now log in.", "success");
      switchForm("login");
      signupForm.reset();
    } else {
      showMessage(data.message || "Signup failed", "error");
    }
  } catch (err) {
    showMessage("Error signing up: " + err.message, "error");
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
      showMessage(data.message || "Failed to load transactions", "error");
      transactions = [];
      updateTransactionList();
      updateSummary();
    }
  } catch (err) {
    showMessage("Error loading transactions: " + err.message, "error");
    transactions = [];
    updateTransactionList();
    updateSummary();
  }
}

async function addTransaction(e) {
  e.preventDefault();
  if (!currentUser) {
    showMessage("Please log in to add transactions.", "error");
    return;
  }

  const description = descriptionEl.value.trim();
  const amount = parseFloat(amountEl.value);

  if (!description || isNaN(amount) || amount === 0) {
    showMessage("Please enter a valid description and amount.", "error");
    return;
  }

  try {
    const res = await fetch(`${BACKEND_URL}/api/transactions/request`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ userId: currentUser, description, amount }),
    });

    const data = await res.json();

    if (res.ok && data.success) {
      showMessage("Transaction added successfully", "success");
      await loadTransactions();
      transactionFormEl.reset();
    } else {
      showMessage(data.message || "Failed to add transaction", "error");
    }
  } catch (err) {
    showMessage("Error adding transaction: " + err.message, "error");
  }
}


async function removeTransaction(id) {
  try {
    const res = await fetch(`${BACKEND_URL}/api/transactions/${id}`, { method: "DELETE" });
    const data = await res.json();

    if (res.ok && data.success) {
      showMessage("Transaction removed", "success");
      await loadTransactions();
    } else {
      showMessage(data.message || "Failed to remove transaction", "error");
    }
  } catch (err) {
    showMessage("Error removing transaction: " + err.message, "error");
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

loginForm.addEventListener("submit", element => {
  element.preventDefault();
  login();
});

signupForm.addEventListener("submit", element => {
  element.preventDefault();
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