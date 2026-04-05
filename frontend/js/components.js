(function renderSharedLayout() {
    const isAuthenticated = Boolean(localStorage.getItem("userId"));
    const analyzeHref = isAuthenticated ? "./upload.html" : "./login.html";
    const navTarget = document.getElementById("navbarMount");
    if (navTarget) {
        const authLinks = isAuthenticated
            ? `
                <a class="nav-link" href="./dashboard.html">Dashboard</a>
                <button id="logoutBtn" class="nav-link nav-logout" type="button">Logout</button>
            `
            : `
                <a class="nav-link" href="./login.html">Login</a>
                <a class="nav-link" href="./register.html">Register</a>
            `;

        navTarget.innerHTML = `
            <header class="topbar">
                <div class="container topbar-inner">
                    <a class="brand" href="./index.html">Resume<span>Fit</span></a>
                    <nav class="nav-links">
                        <a class="nav-link" href="${analyzeHref}">Analyze</a>
                        <a class="nav-link" href="./pricing.html">Pricing</a>
                        ${authLinks}
                    </nav>
                </div>
            </header>
        `;

        const logoutBtn = document.getElementById("logoutBtn");
        if (logoutBtn) {
            logoutBtn.addEventListener("click", () => {
                ["userId", "plan", "email"].forEach((k) => localStorage.removeItem(k));
                window.location.href = "./login.html";
            });
        }
    }

    const cta = document.getElementById("analyzeCta");
    if (cta) cta.setAttribute("href", analyzeHref);

    if (!isAuthenticated) {
        const protectedLinks = document.querySelectorAll('a[href="./upload.html"], a[href="upload.html"]');
        protectedLinks.forEach((link) => {
            link.setAttribute("href", "./login.html");
            link.addEventListener("click", (e) => {
                e.preventDefault();
                window.location.href = "./login.html";
            });
        });
    }

    const footerTarget = document.getElementById("footerMount");
    if (footerTarget) {
        footerTarget.innerHTML = `
            <footer class="footer">
                <div class="container">
                    ResumeFit Micro-SaaS | Fast resume to job matching for students
                </div>
            </footer>
        `;
    }
})();
