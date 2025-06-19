document.addEventListener("DOMContentLoaded", () => {
  const createContactButton = document.querySelector(".create-contact-btn");
  const contactsTableBody = document.querySelector(".contacts-table tbody");
  const contactCountDisplays = document.querySelectorAll(".count");
  const searchInputField = document.querySelector(".search-bar");
  const loginFormElement = document.getElementById("login-form");
  const signupFormElement = document.getElementById("signup-form");
  const loginSignupSection = document.getElementById("access-container");
  const mainContactsSection = document.querySelector(".main");
  const logoutButton = document.querySelector(".logout-btn");

  let loggedInUserEmail = null;

  window.switchForm = function (formType) {
    if (formType === "signup") {
      loginFormElement.classList.remove("active");
      signupFormElement.classList.add("active");
    } else {
      signupFormElement.classList.remove("active");
      loginFormElement.classList.add("active");
    }
  };

function showMessage(message, type = "success", context = "main") {
  const containers = {
    login: document.getElementById("login-message-container"),
    signup: document.getElementById("signup-message-container"),
    main: document.getElementById("main-message-container")
  };

  const container = containers[context];
  if (!container) return;

  const msg = document.createElement("div");
  msg.className = `message ${type}`;
  msg.textContent = message;
  container.appendChild(msg);
  setTimeout(() => msg.remove(), 5000);
}

  const getUserStorageKey = (email) => `contacts_${email}`;

  const saveAllContacts = () => {
    const rows = contactsTableBody.querySelectorAll("tr:not(.contacts-label)");
    const contacts = Array.from(rows).map(row => ({
      name: row.children[0].textContent,
      email: row.children[1].textContent,
      phone: row.children[2].textContent,
      job: row.children[3].textContent
    }));
    localStorage.setItem(getUserStorageKey(loggedInUserEmail), JSON.stringify(contacts));
  };

  const loadAllContacts = () => {
    const savedContacts = JSON.parse(localStorage.getItem(getUserStorageKey(loggedInUserEmail))) || [];
    contactsTableBody.innerHTML = "";
    savedContacts.forEach(contact => addContactToTable(contact));
    enableDeleteButtons();
    updateContactCount();
  };

  const updateContactCount = () => {
    const visibleRows = contactsTableBody.querySelectorAll("tr:not(.contacts-label)");
    contactCountDisplays.forEach(display => {
      display.textContent = `(${visibleRows.length})`;
    });
  };

  const addContactToTable = (contact) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${contact.name}</td>
      <td>${contact.email}</td>
      <td>${contact.phone}</td>
      <td>${contact.job}</td>
      <td>
        <button class="delete-btn">Delete</button>
      </td>
    `;
    contactsTableBody.appendChild(row);
  };

  const enableDeleteButtons = () => {
    contactsTableBody.querySelectorAll(".delete-btn").forEach(button => {
      button.onclick = () => {
        if (confirm("Are you sure you want to delete this contact?")) {
          button.closest("tr").remove();
          saveAllContacts();
          updateContactCount();
          showMessage("Contact deleted", "success");
        }
      };
    });
  };

  const openContactForm = () => {
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
      </div>
    `;

    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) overlay.remove();
    });

    document.body.appendChild(overlay);

    document.getElementById("save-contact").onclick = () => {
      const name = document.getElementById("contact-name").value.trim();
      const phone = document.getElementById("contact-phone").value.trim();
      const email = document.getElementById("contact-email").value.trim();
      const job = document.getElementById("contact-job").value.trim();

      if (!name || !phone) {
        showMessage("Name and phone number are required.", "error");
        return;
      }

      if (email && !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/.test(email)) {
        showMessage("Please enter a valid email address.", "error");
        return;
      }

      addContactToTable({ name, phone, email, job });
      showMessage("Contact added", "success");

      overlay.remove();
      enableDeleteButtons();
      saveAllContacts();
      updateContactCount();
    };

    document.getElementById("cancel-contact").onclick = () => overlay.remove();
  };

  createContactButton.onclick = () => openContactForm();

  function validatePasswordLength(password) {
    return password.length >= 4 && password.length <= 16;
  }

  signupFormElement.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("signup-email").value.trim();
    const password = document.getElementById("signup-password").value.trim();
    const confirmPassword = document.getElementById("signup-confirm-password").value.trim();

    if (!email || !password || !confirmPassword) {
      showMessage("All fields are required.", "error");
      return;
    }

    if (!validatePasswordLength(password)) {
      showMessage("Password must be between 4 and 16 characters.", "error");
      return;
    }

    if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/.test(email)) {
      showMessage("Please enter a valid email address.", "error");
      return;
    }

    if (password !== confirmPassword) {
      showMessage("Passwords do not match.", "error");
      return;
    }

    let allUsers = JSON.parse(localStorage.getItem("users")) || [];
    const userAlreadyExists = allUsers.find(user => user.email === email);

    if (userAlreadyExists) {
      showMessage("User already exists. Please log in.", "error");
      return;
    }

    allUsers.push({ email, password });
    localStorage.setItem("users", JSON.stringify(allUsers));

    showMessage("Signup successful. Please log in.", "success");
    switchForm("login");
  });

  loginFormElement.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-password").value.trim();

    const allUsers = JSON.parse(localStorage.getItem("users")) || [];
    const matchedUser = allUsers.find(user => user.email === email && user.password === password);

    if (!matchedUser) {
      showMessage("Invalid email or password.", "error");
      return;
    }

    loggedInUserEmail = matchedUser.email;
    localStorage.setItem("currentUser", loggedInUserEmail);

    loginSignupSection.style.display = "none";
    mainContactsSection.style.display = "flex";
    loadAllContacts();
    showMessage("Login successful!", "success");
  });

  logoutButton.onclick = () => {
    localStorage.removeItem("currentUser");
    loggedInUserEmail = null;
    mainContactsSection.style.display = "none";
    loginSignupSection.style.display = "flex";
    showMessage("Logged out successfully.", "success");
  };

  searchInputField.addEventListener("input", (e) => {
    const searchValue = e.target.value.toLowerCase();
    contactsTableBody.querySelectorAll("tr:not(.contacts-label)").forEach(row => {
      const cells = Array.from(row.children).slice(0, 4);
      const matches = cells.some(cell => cell.textContent.toLowerCase().includes(searchValue));
      row.style.display = matches ? "" : "none";

      if (matches && searchValue) {
        cells.forEach(cell => {
          const originalText = cell.textContent;
          const regex = new RegExp(`(${searchValue})`, "gi");
          cell.innerHTML = originalText.replace(regex, `<mark>$1</mark>`);
        });
      } else {
        cells.forEach(cell => {
          cell.innerHTML = cell.textContent;
        });
      }
    });
  });

  const savedLoggedInUser = localStorage.getItem("currentUser");
  if (savedLoggedInUser) {
    loggedInUserEmail = savedLoggedInUser;
    loginSignupSection.style.display = "none";
    mainContactsSection.style.display = "flex";
    loadAllContacts();
  }
});
