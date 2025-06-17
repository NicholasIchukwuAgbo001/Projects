document.addEventListener("DOMContentLoaded", () => {
  const createBtn = document.querySelector(".create-contact-btn");
  const tableBody = document.querySelector(".contacts-table tbody");
  const countElement = document.querySelectorAll(".count");
  const searchInput = document.querySelector(".search-bar");
  const loginForm = document.getElementById("login-form");
  const signupForm = document.getElementById("signup-form");
  const accessContainer = document.getElementById("access-container");
  const mainContainer = document.querySelector(".main");
  const logoutBtn = document.querySelector(".logout-btn");

  let currentUser = null;

  window.switchForm = function (form) {
    if (form === "signup") {
      loginForm.classList.remove("active");
      signupForm.classList.add("active");
    } else {
      signupForm.classList.remove("active");
      loginForm.classList.add("active");
    }
  };

  const getUserKey = (email) => `contacts_${email}`;

  const saveContacts = () => {
    const rows = tableBody.querySelectorAll("tr:not(.contacts-label)");
    const contacts = Array.from(rows).map(row => ({
      name: row.children[0].textContent,
      email: row.children[1].textContent,
      phone: row.children[2].textContent,
      job: row.children[3].textContent
    }));
    localStorage.setItem(getUserKey(currentUser), JSON.stringify(contacts));
  };

  const loadContacts = () => {
    const saved = JSON.parse(localStorage.getItem(getUserKey(currentUser))) || [];
    tableBody.innerHTML = "";
    saved.forEach(contact => createContactRow(contact));
    addDeleteListeners();
    addEditListeners();
    updateCount();
  };

  const updateCount = () => {
    const contactRows = tableBody.querySelectorAll("tr:not(.contacts-label)");
    countElement.forEach(count => {
      count.textContent = `(${contactRows.length})`;
    });
  };

  const createContactRow = (contact) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${contact.name}</td>
      <td>${contact.email}</td>
      <td>${contact.phone}</td>
      <td>${contact.job}</td>
      <td>
        <button class="edit-btn">Edit</button>
        <button class="delete-btn">Delete</button>
      </td>
    `;
    tableBody.appendChild(row);
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

  const addEditListeners = () => {
    tableBody.querySelectorAll(".edit-btn").forEach(btn => {
      btn.onclick = () => {
        const row = btn.closest("tr");
        const contact = {
          name: row.children[0].textContent,
          email: row.children[1].textContent,
          phone: row.children[2].textContent,
          job: row.children[3].textContent,
        };
        showForm(contact, row);
      };
    });
  };

  const showForm = (existing = null, rowToUpdate = null) => {
    const modal = document.createElement("div");
    modal.classList.add("modal-overlay");
    modal.innerHTML = `
      <div class="modal">
        <h3>${existing ? "Edit" : "Create"} Contact</h3>
        <label>Name:<br><input type="text" id="contact-name" value="${existing?.name || ''}" /></label><br><br>
        <label>Phone:<br><input type="text" id="contact-phone" value="${existing?.phone || ''}" /></label><br><br>
        <label>Email (optional):<br><input type="text" id="contact-email" value="${existing?.email || ''}" /></label><br><br>
        <label>Job Title & Company (optional):<br><input type="text" id="contact-job" value="${existing?.job || ''}" /></label><br><br>
        <button id="save-contact">${existing ? "Update" : "Save"}</button>
        <button id="cancel-contact">Cancel</button>
      </div>
    `;

    modal.addEventListener("click", (e) => {
      if (e.target === modal) modal.remove();
    });

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

      if (rowToUpdate) {
        rowToUpdate.innerHTML = `
          <td>${name}</td>
          <td>${email}</td>
          <td>${phone}</td>
          <td>${job}</td>
          <td>
            <button class="edit-btn">Edit</button>
            <button class="delete-btn">Delete</button>
          </td>
        `;
      } else {
        createContactRow({ name, phone, email, job });
      }

      modal.remove();
      addDeleteListeners();
      addEditListeners();
      saveContacts();
      updateCount();
    };

    document.getElementById("cancel-contact").onclick = () => modal.remove();
  };

  createBtn.onclick = () => showForm();

  signupForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("signup-email").value.trim();
    const password = document.getElementById("signup-password").value.trim();
    const confirmPassword = document.getElementById("signup-confirm-password").value.trim();

    if (!email || !password || !confirmPassword) {
      alert("All fields are required.");
      return;
    }

    if (password !== confirmPassword) {
      alert("Passwords do not match.");
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

    currentUser = user.email;
    localStorage.setItem("currentUser", currentUser);

    accessContainer.style.display = "none";
    mainContainer.style.display = "flex";
    loadContacts();
  });

  logoutBtn.onclick = () => {
    localStorage.removeItem("currentUser");
    currentUser = null;
    mainContainer.style.display = "none";
    accessContainer.style.display = "flex";
  };

  searchInput.addEventListener("input", (e) => {
    const query = e.target.value.toLowerCase();
    tableBody.querySelectorAll("tr:not(.favorites-label):not(.contacts-label)").forEach(row => {
      const fields = Array.from(row.children).slice(0, 4);
      const match = fields.some(cell => cell.textContent.toLowerCase().includes(query));
      row.style.display = match ? "" : "none";

      if (match && query) {
        fields.forEach(cell => {
          const text = cell.textContent;
          const regex = new RegExp(`(${query})`, "gi");
          cell.innerHTML = text.replace(regex, `<mark>$1</mark>`);
        });
      } else {
        fields.forEach(cell => {
          cell.innerHTML = cell.textContent; 
        });
      }
    });
  });

  const storedUser = localStorage.getItem("currentUser");
  if (storedUser) {
    currentUser = storedUser;
    accessContainer.style.display = "none";
    mainContainer.style.display = "flex";
    loadContacts();
  }
});
