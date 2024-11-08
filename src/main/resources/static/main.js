$(document).ready(function() {
    let selectedCompanies = [];

    $("#query").on("input", function() {
        const query = $(this).val();
        if (query.length > 2) { // Start search when more than 2 characters are entered
            $.ajax({
                url: "/search",
                type: "GET",
                data: { query: query },
                success: function(results) {
                    $("#suggestions").empty().removeClass("hidden");
                    results.forEach(function(company) {
                        $("#suggestions").append(
                            `<div class="company-result px-4 py-3 hover:bg-gray-100 cursor-pointer" data-id="${company.registrikood}" data-name="${company.nimi}">
                                    <span class="font-medium text-gray-800">${company.nimi}</span> (<span class="text-gray-600">${company.registrikood}</span>)
                                </div>`
                        );
                    });
                }
            });
        } else {
            $("#suggestions").empty().addClass("hidden");
        }
    });

    $("#suggestions").on("click", ".company-result", function() {
        const companyId = $(this).data("id");

        if (!selectedCompanies.includes(companyId)) {
            selectedCompanies.push(companyId);

            $.ajax({
                url: "/company-details",
                type: "GET",
                data: { id: companyId },
                success: function(companyDetails) {
                    $("#comparisonTable").removeClass("hidden");
                    $("#companyInfoTableBody").append(
                        `<tr data-id="${companyDetails.registryCode}" class="hover:bg-gray-50">
                            <td class="border px-4 py-2">${companyDetails.registryCode}</td>
                            <td class="border px-4 py-2">${companyDetails.name}</td>
                            <td class="border px-4 py-2">${companyDetails.employeeCount}</td>
                            <td class="border px-4 py-2">${companyDetails.employerCost}</td>
                            <td class="border px-4 py-2">${companyDetails.socialTax}</td>
                            <td class="border px-4 py-2">${companyDetails.unemploymentInsuranceEmployer}</td>
                            <td class="border px-4 py-2">${companyDetails.brutoSalary}</td>
                            <td class="border px-4 py-2">${companyDetails.incomeTax}</td>
                            <td class="border px-4 py-2">${companyDetails.pension}</td>
                            <td class="border px-4 py-2">${companyDetails.unemploymentInsuranceEmployee}</td>
                            <td class="border px-4 py-2">${companyDetails.netoSalary}</td>
                            <td class="border px-4 py-2 text-center">
                                <button class="remove-company bg-red-500 text-white px-3 py-1 rounded-md hover:bg-red-600 transition duration-200">Remove</button>
                            </td>
                        </tr>`
                    );
                    // Clear the search field and hide suggestions
                    $("#query").val("");
                    $("#suggestions").empty().addClass("hidden");
                }
            });
        }
    });

    $("#companyInfoTableBody").on("click", ".remove-company", function() {
        const companyId = $(this).closest("tr").data("id");
        selectedCompanies = selectedCompanies.filter(id => id !== companyId);
        $(this).closest("tr").remove();

        if (selectedCompanies.length === 0) {
            $("#comparisonTable").addClass("hidden");
        }
    });
});