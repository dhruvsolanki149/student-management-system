async function enroll(){
  try{
    const studentId=Number(qs('#studentId').value);
    const offeringId=Number(qs('#offeringId').value);
    await API.post('/api/enrollments',{studentId,offeringId});
    toast('Enrolled'); loadEnrollments();
  }catch(e){ toast(e.message,'err'); }
}
async function loadEnrollments(){
  try{
    const s = qs('#filterStudentId').value;
    const o = qs('#filterOfferingId').value;
    let url = '/api/enrollments';
    if(s) url += '?studentId=' + Number(s);
    else if(o) url += '?offeringId=' + Number(o);
    const data = await API.get(url);
    const tbody = qs('#tbl tbody'); tbody.innerHTML='';
    for(const e of data){
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${e.id}</td><td>${e.studentId}</td><td>${e.offeringId}</td>`;
      tbody.appendChild(tr);
    }
  }catch(e){ toast(e.message,'err'); }
}
qs('#btnEnroll').onclick = enroll;
qs('#btnFilter').onclick = loadEnrollments;
qs('#btnReload') && (qs('#btnReload').onclick = loadEnrollments);
loadEnrollments();
