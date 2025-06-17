document.addEventListener("DOMContentLoaded", () => {
  const createBtn = document.querySelector(".create-contact-btn");
  const tableBody = document.querySelector(".contacts-table tbody");
  const countElement = document.querySelectorAll(".count");
  const searchInput = document.querySelector(".search-bar");
  const loginForm = document.getElementById("login-form");
  const signupForm = document.getElementById("signup-form");
  const accessContainer = document.getElementById("access-container");
  const mainContainer = document.querySelector(".main");

  window.switchForm = function (form) {
    if (form === "signup") {
      loginForm.classList.remove("active");
      signupForm.classList.add("active");
    } else {
      signupForm.classList.remove("active");
      loginForm.classList.add("active");
    }
  };

  signupForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("signup-email").value.trim();
    const password = document.getElementById("signup-password").value.trim();

    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }

    let users = JSON.parse(localStorage.getItem("users")) || [];
    const existingUser = users.find(user => user.email === email);

    if (existingUser) {
      alert("User already exists. Please log in.");
      return;
    }

    users.push({ email, password });
    localStorage.setItem("users", JSON.stringify(users));

    alert("Signup successful. Please log in.");
    switchForm("login");
  });

  loginForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-password").value.trim();

    const users = JSON.parse(localStorage.getItem("users")) || [];
    const user = users.find(u => u.email === email && u.password === password);

    if (!user) {
      alert("Invalid email or password.");
      return;
    }

    accessContainer.style.display = "none";
    mainContainer.style.display = "flex";
    loadContacts();
  });

  const updateCount = () => {
    const contactRows = tableBody.querySelectorAll("tr:not(.contacts-label)");
    countElement.forEach(count => {
      count.textContent = `(${contactRows.length})`;
    });
  };

  const saveContacts = () => {
    const rows = tableBody.querySelectorAll("tr:not(.contacts-label)");
    const contacts = Array.from(rows).map(row => {
      return {
        name: row.children[0].textContent,
        email: row.children[1].textContent,
        phone: row.children[2].textContent,
        job: row.children[3].textContent
      };
    });
    localStorage.setItem("contacts", JSON.stringify(contacts));
  };

  const loadContacts = () => {
    const saved = JSON.parse(localStorage.getItem("contacts")) || [];
    tableBody.innerHTML = ""; 
    saved.forEach(contact => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${contact.name}</td>
        <td>${contact.email}</td>
        <td>${contact.phone}</td>
        <td>${contact.job}</td>
        <td><button class="delete-btn">Delete</button></td>
      `;
      tableBody.appendChild(row);
    });
    addDeleteListeners();
    updateCount();
  };

  const addDeleteListeners = () => {
    tableBody.querySelectorAll(".delete-btn").forEach(btn => {
      btn.onclick = () => {
        if (confirm("Are you sure you want to delete this contact?")) {
          btn.closest("tr").remove();
          saveContacts();
          updateCount();
        }
      };
    });
  };

  const showForm = () => {
    const modal = document.createElement("div");
    modal.classList.add("modal-overlay");
    modal.innerHTML = `
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

    document.body.appendChild(modal);

    document.getElementById("save-contact").onclick = () => {
      const name = document.getElementById("contact-name").value.trim();
      const phone = document.getElementById("contact-phone").value.trim();
      const email = document.getElementById("contact-email").value.trim();
      const job = document.getElementById("contact-job").value.trim();

      if (!name || !phone) {
        alert("Name and phone number are required.");
        return;
      }

      if (email && !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/.test(email)) {
        alert("Please enter a valid email address.");
        return;
      }

      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${name}</td>
        <td>${email}</td>
        <td>${phone}</td>
        <td>${job}</td>
        <td><button class="delete-btn">Delete</button></td>
      `;

      tableBody.appendChild(row);
      addDeleteListeners();
      saveContacts();
      updateCount();
      modal.remove();
    };

    document.getElementById("cancel-contact").onclick = () => modal.remove();
  };

  createBtn.onclick = showForm;

  searchInput.addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase();
    tableBody.querySelectorAll("tr:not(.favorites-label):not(.contacts-label)").forEach(row => {
      const name = row.children[0].textContent.toLowerCase();
      const email = row.children[1].textContent.toLowerCase();
      const phone = row.children[2].textContent.toLowerCase();
      const job = row.children[3].textContent.toLowerCase();
      row.style.display = (
        name.includes(query) ||
        phone.includes(query) ||
        email.includes(query) ||
        job.includes(query)
      ) ? "" : "none";
    });
  });
});
