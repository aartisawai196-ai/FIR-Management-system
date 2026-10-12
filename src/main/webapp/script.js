

const firForm = document.getElementById("firForm");

if (firForm) {
    firForm.addEventListener("submit", function(e) {
        e.preventDefault(); // Stop default form submission

        const formData = new FormData(firForm);

        fetch(firForm.action, {
            method: "POST",
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            // Show alert on the same page
            alert(data.message);

            // Reset form for both success and error
            firForm.reset();
        })
        .catch(err => {
            console.error(err);
            alert("Error submitting FIR. Try again!");
            firForm.reset();
        });
    });
}

// ------------------ REGISTER FIR ------------------


// ------------------ TRACK FIR ------------------
console.log("Track JS loaded"); 

const trackForm = document.getElementById("trackForm");
const trackResult = document.getElementById("trackResult");

trackForm.addEventListener("submit", function(e){
    e.preventDefault(); // page reload rokaycha

    const firNo = document.getElementById("trackFIRNo").value.trim();
    if(firNo === "") {
        trackResult.innerHTML = `<p style="color:red">Please enter FIR number</p>`;
        return;
    }

    fetch(trackForm.action, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `firNo=${encodeURIComponent(firNo)}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.error){
            trackResult.innerHTML = `<p style="color:red; font-weight:bold;">${data.error}</p>`;
        } else {
            trackResult.innerHTML = `
                <div style="padding:15px; background:#e0e0ff; border-radius:12px; box-shadow:0 3px 10px rgba(0,0,0,0.2);">
                    <h3>FIR Status</h3>
                    <p><strong>FIR Number:</strong> ${data.firNo}</p>
                    <p><strong>Status:</strong> ${data.status}</p>
                </div>
            `;
        }
    })
    .catch(err => {
        console.error("Error fetching FIR:", err);
        trackResult.innerHTML = `<p style="color:red; font-weight:bold;">Error connecting to server</p>`;
    });
});

// ------------------ AADHAAR FIR HISTORY ------------------
function getAadhaar(){

    const aadhaar = document.getElementById("aadhaarNo").value.trim();
    const aadhaarResult = document.getElementById("aadhaarResult");

    if(aadhaar === ""){
        aadhaarResult.innerHTML = "<p style='color:red'>Enter Aadhaar number</p>";
        return;
    }

    fetch("http://localhost:9090/FIR_SYSTEM/aadhaarFIR", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "aadhaarNo=" + aadhaar
    })
    .then(res => res.json())
    .then(data => {

        if(data.length === 0){
            aadhaarResult.innerHTML = "<p style='color:red'>No FIR found</p>";
        } else {

            let html = `
            <table style="width:100%; border-collapse: collapse; margin-top:15px;">
                <tr style="background:#333; color:white;">
                    <th style="padding:10px; border:1px solid #ccc;">FIR No</th>
                    <th style="padding:10px; border:1px solid #ccc;">Crime Type</th>
                    <th style="padding:10px; border:1px solid #ccc;">Status</th>
                </tr>
            `;

            data.forEach(fir => {

                let color = "black";

                if(fir.status.toLowerCase() === "completed"){
                    color = "green";
                } else if(fir.status.toLowerCase() === "pending"){
                    color = "orange";
                } else {
                    color = "red";
                }

                html += `
                <tr>
                    <td style="padding:10px; border:1px solid #ccc;">${fir.firNo}</td>
                    <td style="padding:10px; border:1px solid #ccc;">${fir.crimeType}</td>
                    <td style="padding:10px; border:1px solid #ccc; color:${color}; font-weight:bold;">
                        ${fir.status}
                    </td>
                </tr>
                `;
            });

            html += "</table>";

            aadhaarResult.innerHTML = html;
        }
    })
    .catch(err => {
        console.error(err);
        aadhaarResult.innerHTML = "<p style='color:red'>Server error</p>";
    });
}

// ------------------ REPORT FIR ------------------
const reportBtn = document.getElementById("reportBtn");
if(reportBtn){
    reportBtn.addEventListener("click", function(){
        const firNo = document.getElementById("firReportNo").value;
        fetch(`/FIR_SYSTEM/reportFIR?firNo=${firNo}`)
            .then(res => res.json())
            .then(data => {
                const reportDiv = document.getElementById("reportResult");
                const downloadBtn = document.getElementById("downloadPDF");
                if(data.error){
                    reportDiv.innerHTML = `<p style="color:red">${data.error}</p>`;
                    downloadBtn.style.display = "none";
                } else {
                    reportDiv.innerHTML = `
                        <p><strong>FIR Number:</strong> ${data.firNo}</p>
                        <p><strong>Name:</strong> ${data.fullName}</p>
                        <p><strong>Aadhaar:</strong> ${data.aadhaar}</p>
                        <p><strong>Crime Type:</strong> ${data.crimeType}</p>
                        <p><strong>Category:</strong> ${data.crimeCategory}</p>
                        <p><strong>Date:</strong> ${data.incidentDate}</p>
                        <p><strong>Status:</strong> ${data.status}</p>
                        <p><strong>Description:</strong> ${data.description}</p>
                    `;
                    downloadBtn.style.display = "block";
                }
            });
    });
}

const downloadBtn = document.getElementById("downloadPDF");
if(downloadBtn){
    downloadBtn.addEventListener("click", function(){
        const firNo = document.getElementById("firReportNo").value;
        window.location.href = `downloadFIR?firNo=${firNo}`;
    });
}

function submitFIR(event) {
    event.preventDefault();

    let form = event.target;
    let formData = new FormData(form);

    fetch("registerFIR", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        alert(data.message);  // 🔥 POPUP
        form.reset();
    })
    .catch(err => alert("Error submitting FIR"));
}