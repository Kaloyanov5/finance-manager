const API_BASE_URL = "http://localhost:8080/api";

const loginTab = document.getElementById('login-tab');
const registerTab = document.getElementById('register-tab');
const loginForm = document.getElementById('login-form-content');
const registerForm = document.getElementById('register-form-content');

const transactionModal = document.getElementById("transaction-modal");
const dashboard = document.getElementById("dashboard");

let jwtToken = "";

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

document.getElementById('login-form').addEventListener('submit', async function(e) {
    e.preventDefault();
    const email = document.querySelector('#login-form input[type="email"]').value;
    const password = document.querySelector('#login-form input[type="password"]').value;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify( { email, password })
        });

        if (response.ok) {
            const data = await response.json();
            jwtToken = data.token;
            document.getElementById('auth-modal').classList.add('hidden');
            document.getElementById('dashboard').classList.remove('hidden');
            const transactions = await getTransactions();
            await getUsername();
            await updateTransactionTable(transactions);
            await updateChartData(transactions);
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

document.getElementById('register-form').addEventListener('submit', async function (e) {
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

document.getElementById("add-transaction").addEventListener("click", () => {
    transactionModal.classList.remove("hidden");
    dashboard.classList.add("pointer-events-none", "opacity-50");
});

document.getElementById("cancel-transaction").addEventListener("click",  () => {
    transactionModal.classList.add("hidden");
    dashboard.classList.remove("pointer-events-none", "opacity-50");
})

document.getElementById('transaction-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const description = document.getElementById("trans-description").value;
    const amount = document.getElementById("trans-amount").value;
    const date = document.getElementById("trans-date").value;

    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}`
            },
            body: JSON.stringify({ description, amount, date })
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

document.getElementById('send-chat').addEventListener('click', function() {
    const chatInput = document.getElementById('chat-input');
    if (chatInput.value.trim() !== "") {
      const chatDiv = document.createElement('div');
      chatDiv.className = 'chat-bubble';
      chatDiv.innerText = "Bot: " + "Here's a tip on saving money...";
      document.getElementById('chatbot').appendChild(chatDiv);
      chatInput.value = "";
    }
});

const getUsername = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/username`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}`
            }
        });

        if (response.ok) {
            const name = await response.text();
            document.getElementById("username").innerText = name;
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

const getTransactions = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/transactions`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}`
            }
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
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${jwtToken}`
            }
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
        spentText.innerHTML = `You spent <strong>$${totalAmountSpent}</strong> in total. Your money was spent on:`;

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