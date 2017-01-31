/**
 * method toggle for ci jobs div
 * @returns {undefined}
 */
function doJobsToggle(scmId) {
    AJS.$("#scmactivityjobdiv_"+scmId).stop().slideToggle();
}

function doJobsToggleCF(scmId) {
    AJS.$("#scmactivityjobdivCF_"+scmId).stop().slideToggle();
}

function doJobsToggleCFC(scmId) {
    AJS.$("#scmactivityjobdivCFC_"+scmId).stop().slideToggle();
}

/**
 * method toggle for affects div
 * @returns {undefined}
 */
function doAffectsToggle(scmId) {
    AJS.$("#scmactivityaffectdiv_"+scmId).stop().slideToggle();
}

function doAffectsToggleCF(scmId) {
    AJS.$("#scmactivityaffectdivCF_"+scmId).stop().slideToggle();
}

function doAffectsToggleCFC(scmId) {
    AJS.$("#scmactivityaffectdivCFC_"+scmId).stop().slideToggle();
}