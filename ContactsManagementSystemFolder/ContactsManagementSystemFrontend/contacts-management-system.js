const BASE_URL = "http://localhost:4466/api";

const createContactButton = document.querySelector(".create-contact-btn");
const contactsTableBody = document.querySelector(".contacts-table tbody");
const contactCountDisplays = document.querySelectorAll(".count");
const searchInputField = document.querySelector(".search-bar");
const loginFormElement = document.getElementById("login-form");
const signupFormElement = document.getElementById("signup-form");
const loginSignupSection = document.getElementById("access-container");
const mainContactsSection = document.querySelector(".main");
const logoutButton = document.querySelector(".logout-btn");

const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/;
const phoneRegex = /^[0-9]{7,15}$/;

let currentUser = JSON.parse(localStorage.getItem("currentUser"));

window.switchForm = function (formType) {
  loginFormElement.classList.toggle("active", formType === "login");
  signupFormElement.classList.toggle("active", formType === "signup");
};

function showMessage(message, type = "success", context = "main") {
  const containers = {
    login: document.getElementById("login-message-container"),
    signup: document.getElementById("signup-message-container"),
    main: document.getElementById("main-message-container"),
    modal: document.getElementById("modal-message-container"),
  };
  const container = containers[context];
  if (!container) return;

  const msg = document.createElement("div");
  msg.className = `message ${type}`;
  msg.textContent = message;
  container.appendChild(msg);
  setTimeout(() => msg.remove(), 5000);
}

function updateContactCount() {
  const visibleRows = contactsTableBody.querySelectorAll("tr:not(.contacts-label)");
  contactCountDisplays.forEach(display => {
    display.textContent = `(${visibleRows.length})`;
  });
}

function addContactToTable(contact) {
  const row = document.createElement("tr");
  row.dataset.id = contact.id;
  row.innerHTML = `
    <td>${contact.name}</td>
    <td>${contact.email || ""}</td>
    <td>${contact.phoneNumber}</td>
    <td>${contact.jobTitle || ""}</td>
    <td><button class="delete-btn">Delete</button></td>
  `;
  contactsTableBody.appendChild(row);
}

function enableDeleteButtons() {
  contactsTableBody.querySelectorAll(".delete-btn").forEach(button => {
    button.onclick = async () => {
      const row = button.closest("tr");
      const contactId = row.dataset.id;
      if (!contactId) return;
      if (!confirm("Are you sure you want to delete this contact?")) return;
      try {
        const res = await fetch(`${BASE_URL}/contacts/${contactId}`, { method: "DELETE" });
        const data = await res.json();
        if (data.success) {
          row.remove();
          updateContactCount();
          showMessage("Contact deleted", "success");
        } else {
          showMessage(data.message, "error");
        }
      } catch {
        showMessage("Failed to delete contact.", "error");
      }
    };
  });
}

async function loadAllContacts() {
  if (!currentUser || !currentUser.id) return;
  try {
    const res = await fetch(`${BASE_URL}/contacts/${currentUser.id}`);
    const contacts = await res.json();
    contactsTableBody.innerHTML = "";
    contacts.forEach(addContactToTable);
    enableDeleteButtons();
    updateContactCount();
  } catch {
    showMessage("Failed to load contacts.", "error");
  }
}

function openContactForm() {
  const overlay = document.createElement("div");
  overlay.classList.add("modal-overlay");
  overlay.innerHTML = `
    <div class="modal">
      <h3>Create Contact</h3>
      <label>Name:<br><input type="text" id="contact-name" /></label><br><br>
      <label>Phone:<br><input type="text" id="contact-phone" /></label><br><br>
      <label>Email (optional):<br><input type="text" id="contact-email" /></label><br><br>
      <label>Job Title & Company (optional):<br><input type="text" id="contact-job" /></label><br><br>
      <button id="save-contact">Save</button>
      <button id="cancel-contact">Cancel</button>
      <div id="modal-message-container"></div>
    </div>
  `;
  overlay.addEventListener("click", e => { if (e.target === overlay) overlay.remove(); });
  document.body.appendChild(overlay);

  document.getElementById("save-contact").onclick = async () => {
    const name = document.getElementById("contact-name").value.trim();
    const phoneNumber = document.getElementById("contact-phone").value.trim();
    const email = document.getElementById("contact-email").value.trim();
    const jobTitle = document.getElementById("contact-job").value.trim();
    if (!name) return showMessage("Name is required.", "error", "modal");
    if (!phoneRegex.test(phoneNumber)) return showMessage("Phone must be 7-15 digits.", "error", "modal");
    if (email && !emailRegex.test(email)) return showMessage("Invalid email format.", "error", "modal");
    try {
      const res = await fetch(`${BASE_URL}/contacts/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, phoneNumber, email, jobTitle, userId: currentUser.id }),
      });
      const contact = await res.json();
      if (contact.id) {
        addContactToTable(contact);
        overlay.remove();
        enableDeleteButtons();
        updateContactCount();
        showMessage("Contact added", "success");
      } else {
        showMessage(contact.message || "Failed to save contact.", "error", "modal");
      }
    } catch {
      showMessage("Error saving contact", "error", "modal");
    }
  };
  document.getElementById("cancel-contact").onclick = () => overlay.remove();
}

createContactButton.onclick = openContactForm;

signupFormElement.onsubmit = async e => {
  e.preventDefault();
  const email = document.getElementById("signup-email").value.trim();
  const password = document.getElementById("signup-password").value.trim();
  const confirmPassword = document.getElementById("signup-confirm-password").value.trim();
  if (!emailRegex.test(email)) return showMessage("Invalid email.", "error", "signup");
  if (password.length < 4 || password.length > 16) return showMessage("Password must be 4-16 chars.", "error", "signup");
  if (password !== confirmPassword) return showMessage("Passwords do not match.", "error", "signup");
  try {
    const res = await fetch(`${BASE_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    if (data.success) {
      showMessage("Signup successful. Please log in.", "success", "signup");
      switchForm("login");
    } else {
      showMessage(data.message, "error", "signup");
    }
  } catch {
    showMessage("Signup failed.", "error", "signup");
  }
};

loginFormElement.onsubmit = async e => {
  e.preventDefault();
  const email = document.getElementById("login-email").value.trim();
  const password = document.getElementById("login-password").value.trim();
  if (!emailRegex.test(email)) return showMessage("Invalid email.", "error", "login");
  if (!password) return showMessage("Password required.", "error", "login");
  try {
    const res = await fetch(`${BASE_URL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    if (data.success) {
      currentUser = { id: data.userId, email: data.email };
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
      loginSignupSection.style.display = "none";
      mainContactsSection.style.display = "flex";
      showMessage("Login successful!", "success");
      loadAllContacts();
    } else {
      showMessage(data.message, "error", "login");
    }
  } catch {
    showMessage("Login failed.", "error", "login");
  }
};

logoutButton.onclick = () => {
  localStorage.removeItem("currentUser");
  currentUser = null;
  loginSignupSection.style.display = "flex";
  mainContactsSection.style.display = "none";
  showMessage("Logged out successfully.", "success");
};

searchInputField.oninput = e => {
  const searchValue = e.target.value.toLowerCase();
  contactsTableBody.querySelectorAll("tr:not(.contacts-label)").forEach(row => {
    const cells = Array.from(row.children).slice(0, 4);
    const matches = cells.some(cell => cell.textContent.toLowerCase().includes(searchValue));
    row.style.display = matches ? "" : "none";
    cells.forEach(cell => {
      const original = cell.textContent;
      cell.innerHTML = searchValue
        ? original.replace(new RegExp(`(${searchValue})`, "gi"), `<mark>$1</mark>`)
        : original;
    });
  });
};

if (currentUser && currentUser.id) {
  loginSignupSection.style.display = "none";
  mainContactsSection.style.display = "flex";
  loadAllContacts();
}
