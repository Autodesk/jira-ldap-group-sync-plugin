/* global AJS */

function adskldapsyncconfig_filter() {
  // Declare variables 
  var count, input, filter, table, tr, i;
  count = document.getElementById("adskldapsyncconfig_count");
  input = document.getElementById("adskldapsyncconfig_search");
  filter = input.value.toUpperCase();
  table = document.getElementById("adskldapsync_configs");
  tr = table.getElementsByTagName("tr");

  // Loop through all table rows, and hide those who don't match the search query
  var size = 0;
  for (i = 0; i < tr.length; i++) {
    var td0 = tr[i].getElementsByTagName("td")[1];
    var td1 = tr[i].getElementsByTagName("td")[2];
    if (td0 || td1) {
      if (td0.innerHTML.toUpperCase().indexOf(filter) > -1 || 
              td1.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
        size++;        
      } else {
        tr[i].style.display = "none";        
      }
    }
  }
  count.innerHTML = size;  
}