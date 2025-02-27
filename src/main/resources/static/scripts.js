const API_BASE_URL = "http://localhost:8080/api";
const CHAT_BASE_URL = "http://localhost:8080/chat";

const loginTab = document.getElementById('login-tab');
const registerTab = document.getElementById('register-tab');
const loginForm = document.getElementById('login-form-content');
const registerForm = document.getElementById('register-form-content');

const transactionModal = document.getElementById("transaction-modal");
const dashboard = document.getElementById("dashboard");

// REGISTER/LOGIN MODAL TAB SWITCH

loginTab.addEventListener('click', () => {
    loginForm.classList.remove('invisible-form');
    loginForm.classList.add('visible-form');
    registerForm.classList.remove('visible-form');
    registerForm.classList.add('invisible-form');
    loginTab.classList.add('bg-blue-600', 'text-white');
    registerTab.classList.remove('bg-blue-600', 'text-white');
    registerTab.classList.add('bg-gray-300', 'dark:bg-gray-500', 'text-black');
});

registerTab.addEventListener('click', () => {
    registerForm.classList.remove('invisible-form');
    registerForm.classList.add('visible-form');
    loginForm.classList.remove('visible-form');
    loginForm.classList.add('invisible-form');
    registerTab.classList.add('bg-blue-600', 'text-white');
    loginTab.classList.remove('bg-blue-600', 'text-white');
    loginTab.classList.add('bg-gray-300', 'dark:bg-gray-500', 'text-black');
});

// USER LOGIN REQUEST

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.querySelector('#login-form input[type="email"]').value;
    const password = document.querySelector('#login-form input[type="password"]').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify( { email, password }),
            credentials: "include"
        });

        if (response.ok) {
            await login();
            e.target.reset();
        } else if (response.status === 403 || response.status === 404) {
            alert(await response.text());
        } else {
            alert("Login failed. Please check your credentials.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error logging in:", error);
    }
});

const login = async () => {
    document.getElementById('auth-modal').classList.add('hidden');
    document.getElementById('dashboard').classList.remove('hidden');
    const transactions = await getTransactions();
    await getUsername();
    await updateTransactionTable(transactions);
    await updateChartData(transactions);
    await loadTasks();
}

// USER REGISTER REQUEST

document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.querySelector('#register-form input[type="text"]').value;
    const email = document.querySelector('#register-form input[type="email"]').value;
    const [password, confirmPassword] = Array.from(document.querySelectorAll('#register-form input[type="password"]'))
        .map(e => e.value);

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name, email, password, confirmPassword })
        });

        if (response.ok) {
            alert("Registration complete! You can now log in.");
            loginForm.classList.remove('invisible-form');
            loginForm.classList.add('visible-form');
            registerForm.classList.remove('visible-form');
            registerForm.classList.add('invisible-form');
            loginTab.classList.add('bg-blue-600', 'text-white');
            registerTab.classList.remove('bg-blue-600', 'text-white');
            registerTab.classList.add('bg-gray-300', 'dark:bg-gray-500', 'text-black');
            e.target.reset();
        } else if (response.status === 400) {
            alert(await response.text());
        } else {
            alert("Registration failed. Please check your credentials.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error registering:", error);
    }
});

// USER LOGOUT REQUEST

document.getElementById("logout").addEventListener("click", async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/logout`, {
            method: "POST",
            credentials: "include",
        });

        if (response.ok) {
            window.location.reload();
        } else {
            alert("Failed to logout.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error deleting user:", error);
    }
});

// USER DELETE REQUEST

document.getElementById("delete-user").addEventListener("click", async () => {
    if (confirm("Are you sure you want to delete this user?")) {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/delete`, {
                method: "DELETE",
                credentials: "include"
            });

            if (response.ok) {
                window.location.reload();
            } else if (response.status === 401 || response.status === 404){
                alert(await response.text());
            } else {
                alert("Failed to delete user.");
            }
        } catch (error) {
            alert("Error occurred! Check console for more information!");
            console.error("Error deleting user:", error);
        }
    }
});

// DASHBOARD BUTTONS AND FUNCTION FOR GETTING USERNAME

document.getElementById("add-transaction").addEventListener("click", () => {
    transactionModal.classList.remove("hidden");
    dashboard.classList.add("pointer-events-none", "opacity-50");
});

document.getElementById("cancel-transaction").addEventListener("click",  () => {
    transactionModal.classList.add("hidden");
    dashboard.classList.remove("pointer-events-none", "opacity-50");
});

document.getElementById("user-icon").addEventListener("click", () => {
    const userMenu = document.getElementById("user-menu");
    userMenu.classList.toggle("hidden");
});

const getUsername = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/username`, {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            document.getElementById("username").innerText = await response.text();
        } else if (response.status === 401 || response.status === 404){
            alert(await response.text());
        } else {
            alert("Failed to fetch username.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error fetching data:", error);
    }
};

// TRANSACTION REQUESTS

document.getElementById('transaction-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const description = document.getElementById("trans-description").value.trim();
    const amount = document.getElementById("trans-amount").value.trim();
    const date = document.getElementById("trans-date").value.trim();

    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ description, amount, date }),
            credentials: "include"
        });

        if (response.ok) {
            transactionModal.classList.add('hidden');
            dashboard.classList.remove('pointer-events-none', 'opacity-50');
            const transactions = await getTransactions();
            await updateTransactionTable(transactions);
            await updateChartData(transactions);
            e.target.reset();
        } else if (response.status === 401) {
            alert(await response.text());
        } else {
            alert("Failed adding a transaction. Please check your input.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error adding a transaction:", error);
    }
});

const getTransactions = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            return await response.json();
        } else if (response.status === 401) {
            alert(await response.text());
        } else {
            alert("Failed to load transactions.");
        }

        return null;
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error fetching data:", error);
    }
};

const updateTransactionTable = async (transactions) => {
    if (transactions !== null) {
        transactions = transactions.sort((a, b) => new Date(b.date) - new Date(a.date));
        const transactionTable = document.getElementById("transaction-table");
        transactionTable.innerHTML = transactions.map(transaction => `
            <tr class="border-b">
              <td class="px-2 py-1">${transaction.description}</td>
              <td class="px-2 py-1">$${transaction.amount.toFixed(2)}</td>
              <td class="px-2 py-1">${transaction.date}</td>
              <td class="px-2 py-1">${transaction.category}</td>
              <td class="px-2 py-1"><button class="text-red-500 delete-btn" onclick="deleteTransaction(${transaction.id})">Delete</button></td>
            </tr>
        `).join("");
    }
};

const deleteTransaction = async (id) => {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions/${id}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (response.ok) {
            const transactions = await getTransactions();
            await updateTransactionTable(transactions);
            await updateChartData(transactions);
        } else if (response.status === 401 || response.status === 404) {
            alert(await response.text());
        } else {
            alert("Failed to delete transaction.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error deleting transaction:", error);
    }
};

// CHART FUNCTIONS

const ctx = document.getElementById('pieChart').getContext('2d');
const pieChart = new Chart(ctx, {
    type: 'pie',
    data: {
        labels: [
            "Groceries", "Rent", "Entertainment", "Shopping",
            "Food", "Travel", "Gift", "Personal", "Savings"
        ],
        datasets: [{
            data: [5, 5, 5, 5, 5, 5, 5, 5, 5],
            backgroundColor: [
                '#F87171', '#60A5FA', '#34D399', '#FBBF24',
                '#F472B6', '#A78BFA', '#10B911', '#FACC95', '#22D3EE'
            ],
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
    }
});

const updateChartData = async (transactions) => {
    const chartCanvas = document.getElementById("chart-div");
    transactions = transactions.sort((a, b) => b.amount - a.amount);

    const categoryTotals = transactions.reduce((totals, transaction) => {
        if (!totals[transaction.category]) {
            totals[transaction.category] = 0;
        }
        totals[transaction.category] += transaction.amount;
        return totals;
    }, {});

    const totalAmountSpent = transactions.reduce((total, transaction) => {
        return total + transaction.amount;
    }, 0);

    const spentText = document.getElementById("spent-text");
    if (totalAmountSpent === 0) {
        chartCanvas.classList.add("hidden");
        spentText.innerHTML = "You have no transactions.";
    } else {
        chartCanvas.classList.remove("hidden");
        spentText.innerHTML = `You spent <strong>$${totalAmountSpent.toFixed(2)}</strong> in total. Your money was spent on:`;

        const categorySpentList = document.getElementById("category-percent-list");
        categorySpentList.innerHTML = Object.entries(categoryTotals).map(([category, amount]) => {
            const percentage = ((amount / totalAmountSpent) * 100).toFixed(2);
            return `<li>${category}: ${percentage}%</li>`;
        }).join("");

        pieChart.data.labels = Object.keys(categoryTotals);
        pieChart.data.datasets[0].data = Object.values(categoryTotals);
        pieChart.update();
    }
}

// TASK REQUESTS

const loadTasks = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/tasks`, {
            method: "GET",
            credentials: "include"
        });

        if (response.ok) {
            const tasks = await response.json();
            if (tasks !== null) {
                const taskList = document.getElementById("task-list");
                taskList.innerHTML = tasks.map(task => `
                <div class="chat-bubble">${task.description} <button onclick="deleteTask(${task.taskId})" class="ml-2 text-red-500 delete-task">x</button></div>
                `).join("");
            }
        } else if (response.status === 401) {
            alert(await response.text());
        } else {
            alert("Failed to load tasks.");
        }

        return null;
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error fetching data:", error);
    }
}

const deleteTask = async (id) => {
    console.log(id);
    try {
        const response = await fetch(`${API_BASE_URL}/tasks/${id}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (response.ok) {
            await loadTasks();
        } else if (response.status === 401 || response.status === 404) {
            alert(await response.text());
        } else {
            alert("Failed to delete task.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error deleting task:", error);
    }
}

document.getElementById("add-task").addEventListener("click", async () => {
    const descriptionInput = document.getElementById("new-task");
    const description = descriptionInput.value.trim();

    if (description === "") return;

    try {
        const response = await fetch(`${API_BASE_URL}/tasks`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: description,
            credentials: "include"
        });

        if (response.ok) {
            await loadTasks();
        } else if (response.status === 401) {
            alert(await response.text());
        } else {
            alert("Failed adding a task. Please check your input.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error adding a task:", error);
    }
    descriptionInput.value = "";
});

// CHATBOT REQUESTS

document.getElementById("send-chat").addEventListener("click", async () => {
    const chatInput = document.getElementById("chat-input");
    const userMessage = chatInput.value.trim();

    if (userMessage === "") return;

    const userDiv = document.createElement("div");
    userDiv.className = "chat-bubble";
    userDiv.innerText = "You: " + userMessage;
    document.getElementById('chatbot').appendChild(userDiv);

    try {
        const response = await fetch(`${CHAT_BASE_URL}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: userMessage,
            credentials: "include"
        });

        if (response.ok) {
            const botResponse = await response.text();
            const chatDiv = document.createElement("div");
            chatDiv.className = "chat-bubble";
            chatDiv.innerText = "Bot: " + botResponse;
            document.getElementById('chatbot').appendChild(chatDiv);
        } else if (response.status === 422) {
            await delay(800);
            const chatDiv = document.createElement("div");
            chatDiv.className = "chat-bubble";
            chatDiv.innerText = "Bot: You have no transactions available.";
            document.getElementById('chatbot').appendChild(chatDiv);
        } else if (response.status === 401 || response.status === 500) {
            alert(await response.text());
        }  else {
            alert("Failed to communicate with the chatbot. Please check your message request.");
        }
    } catch (error) {
        alert("Error occurred! Check console for more information!");
        console.error("Error communicating with AI:", error);
    }
    chatInput.value = "";
});

// DELAY FUNCTION

const delay = ms => new Promise(res => setTimeout(res, ms));

// ON PAGE LOAD

window.onload = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/username`, {
            method: "GET",
            credentials: "include",
        });

        if (response.ok) {
            const username = await response.text();
            console.log("User is logged in as:", username);
            await login();
        } else {
            console.log("User is not logged in, staying on login page");
        }
    } catch (error) {
        console.error("Error checking login status:", error);
    }
};