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
const searchInput = document.getElementById("search");

let transactions = [];
let currentUser = localStorage.getItem("currentUser");

const showAccessContainer = () => {
  document.getElementById("access-container").style.display = "flex";
  document.querySelector(".container").style.display = "none";
};

const showAppContainer = (userName = "") => {
  const accessContainer = document.getElementById("access-container");
  const appContainer = document.querySelector(".container");

  accessContainer.style.display = "none";
  appContainer.style.display = "block";

  let welcomeMessageEl = document.getElementById("welcome-message");
  if (!welcomeMessageEl) {
  welcomeMessageEl = document.createElement("h2");
  welcomeMessageEl.id = "welcome-message";

  welcomeMessageEl.style.textAlign = "center";
  welcomeMessageEl.style.margin = "1rem 0";
  welcomeMessageEl.style.color = "#d3c7c7";

  const heading = appContainer.querySelector("h1");
  heading.insertAdjacentElement("beforebegin", welcomeMessageEl);

}

  if (userName) {
    welcomeMessageEl.textContent = `Welcome, ${userName}!`;
  } else {
    welcomeMessageEl.textContent = "";
  }
};

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("currentUser");
  currentUser = null;
  transactions = [];
  showAccessContainer();
  updateTransactionList();
  updateSummary();
});

if (currentUser) {
  let userName = localStorage.getItem("currentUserName");
  if (!userName) {
    const savedUsers = JSON.parse(localStorage.getItem("users")) || [];
    const user = savedUsers.find(u => u.email === currentUser);
    userName = user ? user.name : "";
    if (userName) {
      localStorage.setItem("currentUserName", userName);
    }
  }
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

const resetLoginForm = () => {
  loginForm.reset();
};

const resetSignupForm = () => {
  signupForm.reset();
};

const login = () => {
  const email = document.getElementById("login-email").value.trim();
  const password = document.getElementById("login-password").value.trim();

  if (!email || !password) {
    alert("Please enter both email and password.");
    return;
  }

  const savedUsers = JSON.parse(localStorage.getItem("users")) || [];
  const user = savedUsers.find(u => u.email === email && u.password === password);

if (user) {
  alert(`Welcome ${user.name}`);
  currentUser = email;
  localStorage.setItem("currentUser", currentUser);
  localStorage.setItem("currentUserName", user.name);
  showAppContainer(user.name);
  loadTransactions();
  resetLoginForm();
} else {
  alert("Invalid email or password");
}
};

const signup = () => {
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
    alert("Please enter a valid email address.");
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

  let savedUsers = JSON.parse(localStorage.getItem("users")) || [];
  if (savedUsers.some(user => user.email === email)) {
    alert("User already exists with that email");
    return;
  }

  savedUsers.push({ email, password, name, age });
  localStorage.setItem("users", JSON.stringify(savedUsers));
  alert("Signup successful! You can now log in.");
  switchForm("login");
  resetSignupForm();
};

const validatePasswordLength = (password) => password.length >= 4 && password.length <= 16;
const validateEmail = (email) => /^[a-zA-Z0-9._%+-]+@gmail\.com$/.test(email);

function loadTransactions() {
  try {
    transactions = currentUser
      ? JSON.parse(localStorage.getItem(`transactions_${currentUser}`)) || []
      : [];
  } catch (error) {
    console.error("Error loading transactions:", error);
    transactions = [];
  }
  updateTransactionList();
  updateSummary();
}

function saveTransactions() {
  if (currentUser) {
    localStorage.setItem(`transactions_${currentUser}`, JSON.stringify(transactions));
  }
}

const addTransaction = (element) => {
  element.preventDefault();
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

  transactions.push({ id: crypto.randomUUID(), description, amount });
  saveTransactions();

  searchInput.value = "";

  updateTransactionList();
  updateSummary();

  transactionFormEl.reset();
};

function removeTransaction(id) {
  transactions = transactions.filter(transaction => transaction.id !== id);
  saveTransactions();

  searchInput.value = "";

  updateTransactionList();
  updateSummary();
}

searchInput.addEventListener("input", () => {
  const query = searchInput.value.toLowerCase();
  const filtered = transactions.filter(transaction =>
    transaction.description.toLowerCase().includes(query)
  );
  updateTransactionList(filtered);
  updateSummary();
});

function updateTransactionList(filteredTransactions = transactions) {
  transactionListEl.innerHTML = "";
  [...filteredTransactions].reverse().forEach(transaction => {
    transactionListEl.appendChild(createTransactionElement(transaction));
  });
}

function createTransactionElement(transaction) {
  const liTag = document.createElement("li");
  liTag.classList.add("transaction", transaction.amount > 0 ? "income" : "expense");
  liTag.innerHTML = `
    <span>${transaction.description}</span>
    <span>
      ${formatCurrency(transaction.amount)}
      <button class="delete-btn" onclick="removeTransaction('${transaction.id}')">x</button>
    </span>
  `;
  return liTag;
}

function updateSummary(transactionsForSummary = transactions) {
  const balance = transactionsForSummary.reduce((sum, transaction) => sum + transaction.amount, 0);
  const income = transactionsForSummary
    .filter(transaction => transaction.amount > 0)
    .reduce((sum, transaction) => sum + transaction.amount, 0);
  const expenses = transactionsForSummary
    .filter(transaction => transaction.amount < 0)
    .reduce((sum, transaction) => sum + transaction.amount, 0);

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

loginBtn.addEventListener("click", element => {
  login();
});

signupBtn.addEventListener("click", element => {
  signup();
});

transactionFormEl.addEventListener("submit", addTransaction);
loadTransactions();