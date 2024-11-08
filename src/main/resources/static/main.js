let selectedCompanies = [];

$(document).ready(function () {
    // Attach click event to buttons with class .quarter-button
    $(".quarter-button").on("click", function () {
        // Get the data-id attribute from the clicked button
        const csvId = $(this).data("id");
        $(".quarter-button").removeClass("bg-gray-200").addClass("bg-white");
        $(".quarter-button").removeClass("hover:bg-gray-300").addClass("hover:bg-gray-100");
        $(this).addClass("bg-gray-200 hover:bg-gray-300");

        // Make an AJAX request to /update-csv with the csv parameter
        $.ajax({
            url: "/update-csv",
            type: "GET",
            data: { csv: csvId },
            success: function (response) {
                console.log("Update successful:", response);
                refreshSelectedCompanies();
            },
            error: function (xhr, status, error) {
                console.error("Error during update:", error);
            }
        });
    });

    // Search functionality
    $("#query").on("input", function () {
        const query = $(this).val();
        if (query.length > 2) { // Start search when more than 2 characters are entered
            $.ajax({
                url: "/search",
                type: "GET",
                data: { query: query },
                success: function (results) {
                    $("#suggestions").empty().removeClass("hidden");
                    results.forEach(function (company) {
                        $("#suggestions").append(
                            `<div data-id="${company.registryCode}" class="company-result hover:bg-blue-50 cursor-pointer border-b border-gray-200 last:border-none rounded-lg">
                                <span class="font-medium text-gray-800">${company.name}</span> 
                                (<span class="text-gray-600">${company.registryCode}</span>)
                            </div>`
                        );
                    });
                }
            });
        } else {
            $("#suggestions").empty().addClass("hidden");
        }
    });

    // Click event for selecting a company from the suggestions
    $("#suggestions").on("click", ".company-result", function () {
        const registryCode = $(this).data("id");
        if (!selectedCompanies.includes(registryCode)) {
            selectedCompanies.push(registryCode);

            $.ajax({
                url: "/company-details",
                type: "GET",
                data: { registryCode: registryCode },
                success: function (companyDetails) {
                    $("#comparisonTable").removeClass("hidden");
                    appendCompanyToTable(companyDetails);
                    // Clear the search field and hide suggestions
                    $("#query").val("");
                    $("#suggestions").empty().addClass("hidden");
                }
            });
        }
    });

    // Click event for removing a company from the table
    $("#companyInfoTableBody").on("click", ".remove-company", function () {
        const companyId = $(this).closest("tr").data("id");
        selectedCompanies = selectedCompanies.filter(id => id !== companyId);
        $(this).closest("tr").remove();

        if (selectedCompanies.length === 0) {
            $("#comparisonTable").addClass("hidden");
        }
    });

    // Function to refresh all selected companies
    function refreshSelectedCompanies() {
        // Clear the current selected companies list
        $("#companyInfoTableBody").empty();

        // Fetch details of each company in the selectedCompanies array
        selectedCompanies.forEach(function (registryCode) {
            $.ajax({
                url: "/company-details",
                type: "GET",
                data: { registryCode: registryCode },
                success: function (companyDetails) {
                    $("#comparisonTable").removeClass("hidden");
                    appendCompanyToTable(companyDetails);
                },
                error: function (xhr, status, error) {
                    console.error("Error fetching company details:", error);
                }
            });
        });

        // Hide comparison table if there are no companies selected
        if (selectedCompanies.length === 0) {
            $("#comparisonTable").addClass("hidden");
        }
    }

    // Function to append company details to the table
    function appendCompanyToTable(companyDetails) {
        $("#companyInfoTableBody").append(
            `<tr data-id="${companyDetails.registryCode}">
                <th class="border-t-0 px-6 align-middle border-l-0 border-r-0 text-xs whitespace-nowrap p-4 text-left text-blueGray-700 ">
                    ${companyDetails.name}
                </th>
                <td class="border-t-0 px-6 align-middle border-l-0 border-r-0 text-xs whitespace-nowrap p-4 ">
                    ${companyDetails.employeeCount}
                </td>
                <td class="border-t-0 px-6 align-center border-l-0 border-r-0 text-xs whitespace-nowrap p-4">
                    ${companyDetails.brutoSalary}
                </td>
                <td class="border-t-0 px-6 align-middle border-l-0 border-r-0 text-xs whitespace-nowrap p-4">
                    <i class="fas fa-arrow-up text-emerald-500 mr-4"></i>
                    ${companyDetails.netoSalary}
                </td>                            
                <td class="border-t-0 px-6 align-middle border-l-0 border-r-0 text-xs whitespace-nowrap p-4">
                    <a class="remove-company">
                        <i class="fa-solid fa-trash-can cursor-pointer"></i>
                    </a>
                </td>
            </tr>`
        );
    }
});
