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

const BACKEND_URL = "http://localhost:4455";

function showAccessContainer() {
  document.getElementById("access-container").style.display = "flex";
  document.querySelector(".container").style.display = "none";
}

function showAppContainer(userName = "") {
  const accessContainer = document.getElementById("access-container");
  const appContainer = document.querySelector(".container");

  accessContainer.style.display = "none";
  appContainer.style.display = "block";

  let welcomeMessageEl = document.getElementById("welcome-message");

  welcomeMessageEl.style.textAlign = "center";
  welcomeMessageEl.style.margin = "1rem 0";
  welcomeMessageEl.style.color = "#d3c7c7";

  welcomeMessageEl.textContent = userName ? `Welcome, ${userName}!` : "";
}

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("currentUser");
  localStorage.removeItem("currentUserName");
  currentUser = null;
  transactions = [];
  showAccessContainer();
  updateTransactionList();
  updateSummary();
});

if (currentUser) {
  let userName = localStorage.getItem("currentUserName") || "";
  showAppContainer(userName);
  loadTransactions();
} else {
  showAccessContainer();
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

function resetLoginForm() {
  loginForm.reset();
}

function resetSignupForm() {
  signupForm.reset();
}

function validateEmail(email) {
  return /^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email);
}

function validatePasswordLength(password) {
  return password.length >= 4 && password.length <= 16;
}

async function login() {
  const email = document.getElementById("login-email").value.trim();
  const password = document.getElementById("login-password").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  try {
    const response = await fetch(`${BACKEND_URL}/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (response.ok && data.success) {
      alert(`Welcome ${data.name}`);
      currentUser = email;
      localStorage.setItem("currentUser", currentUser);
      localStorage.setItem("currentUserName", data.name);
      showAppContainer(data.name);
      await loadTransactions();
      resetLoginForm();
    } else {
      alert(data.message || "Invalid email or password");
    }
  } catch (error) {
    alert("Login failed: " + error.message);
  }
}

async function signup() {
  const email = document.getElementById("signup-email").value.trim();
  const password = document.getElementById("signup-password").value.trim();
  const name = document.getElementById("signup-name").value.trim();
  const ageInput = document.getElementById("age").value.trim();
  const age = parseInt(ageInput, 10);

  if (!email || !password || !name || !ageInput) {
    alert("Please fill in all fields.");
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

  if (isNaN(age) || age <= 0) {
    alert("Please enter a valid age.");
    return;
  }

  try {
    const response = await fetch(`${BACKEND_URL}/api/auth/signup`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, name, age }),
    });

    const data = await response.json();

    if (response.ok && data.success) {
      alert("Signup successful! You can now log in.");
      switchForm("login");
      resetSignupForm();
    } else {
      alert(data.message || "Signup failed");
    }
  } catch (error) {
    alert("Error signing up: " + error.message);
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
    const response = await fetch(
      `${BACKEND_URL}/api/transactions/${currentUser}`
    );
    const data = await response.json();

    if (response.ok && data.success) {
      transactions = data.transactions || [];
      updateTransactionList();
      updateSummary();
    } else {
      alert(data.message || "Failed to load transactions");
      transactions = [];
      updateTransactionList();
      updateSummary();
    }
  } catch (error) {
    alert("Error loading transactions: " + error.message);
    transactions = [];
    updateTransactionList();
    updateSummary();
  }
}

async function addTransaction(event) {
  event.preventDefault();

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
    const response = await fetch(`${BACKEND_URL}/api/transactions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: currentUser,
        description,
        amount,
      }),
    });

    const data = await response.json();

    if (response.ok && data.success) {
      alert("Transaction added successfully");
      await loadTransactions();
      transactionFormEl.reset();
    } else {
      alert(data.message || "Failed to add transaction");
    }
  } catch (error) {
    alert("Error adding transaction: " + error.message);
  }
}

async function removeTransaction(id) {
  try {
    const response = await fetch(`${BACKEND_URL}/api/transactions/${id}`, {
      method: "DELETE",
    });
    const data = await response.json();

    if (response.ok && data.success) {
      alert("Transaction removed");
      await loadTransactions();
    } else {
      alert(data.message || "Failed to remove transaction");
    }
  } catch (error) {
    alert("Error removing transaction: " + error.message);
  }
  console.log("Attempting to delete transaction with ID:", id);
}

function updateTransactionList(filteredTransactions = transactions) {
  transactionListEl.innerHTML = "";
  [...filteredTransactions].reverse().forEach((transaction) => {
    transactionListEl.appendChild(createTransactionElement(transaction));
  });
}

function createTransactionElement(transaction) {
  const liTag = document.createElement("li");
  liTag.classList.add(
    "transaction",
    transaction.amount > 0 ? "income" : "expense"
  );

  const descSpan = document.createElement("span");
  descSpan.textContent = transaction.description;

  const amountSpan = document.createElement("span");
  amountSpan.textContent = formatCurrency(transaction.amount);

  const deleteBtn = document.createElement("button");
  deleteBtn.classList.add("delete-btn");
  deleteBtn.textContent = "x";
  deleteBtn.addEventListener("click", () => {
    console.log("Delete clicked for ID:", transaction.id);
    removeTransaction(transaction.id);
  });

  amountSpan.appendChild(deleteBtn);

  liTag.appendChild(descSpan);
  liTag.appendChild(amountSpan);

  return liTag;
}

function updateSummary(filteredTransactions = transactions) {
  const balance = filteredTransactions.reduce((sum, t) => sum + t.amount, 0);
  const income = filteredTransactions
    .filter((t) => t.amount > 0)
    .reduce((sum, t) => sum + t.amount, 0);
  const expenses = filteredTransactions
    .filter((t) => t.amount < 0)
    .reduce((sum, t) => sum + t.amount, 0);

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

searchInput.addEventListener("input", () => {
  const query = searchInput.value.toLowerCase();
  const filtered = transactions.filter((t) =>
    t.description.toLowerCase().includes(query)
  );
  updateTransactionList(filtered);
  updateSummary(filtered);
});

loginForm.addEventListener("submit", (e) => {
  e.preventDefault();
  login();
});

signupForm.addEventListener("submit", (e) => {
  e.preventDefault();
  signup();
});

transactionFormEl.addEventListener("submit", addTransaction);

window.removeTransaction = removeTransaction;

if (currentUser) {
  loadTransactions();
}
