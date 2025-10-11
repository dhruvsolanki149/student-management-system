let selectedId=null;
async function loadStudents(q=""){
  try{
    const data = await API.get('/api/students' + (q ? '?q='+encodeURIComponent(q) : ''));
    const tbody = qs('#tbl tbody'); tbody.innerHTML='';
    for(const s of data){
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${s.id}</td><td>${s.regNo||''}</td><td>${(s.firstName||'')+' '+(s.lastName||'')}</td><td>${s.program||''}</td><td>${s.status||''}</td>`;
      tr.onclick = () => selectRow(s,tr);
      tbody.appendChild(tr);
    }
  }catch(e){ toast(e.message,'err'); }
}
function selectRow(s,tr){
  qsa('#tbl tr').forEach(r=>r.classList.remove('sel'));
  tr.classList.add('sel');
  selectedId = s.id;
  qs('#regNo').value = s.regNo||'';
  qs('#firstName').value = s.firstName||'';
  qs('#lastName').value = s.lastName||'';
  qs('#dob').value = s.dob||'';
  qs('#gender').value = s.gender||'';
  qs('#contact').value = s.contact||'';
  qs('#program').value = s.program||'';
  qs('#status').value = s.status||'ACTIVE';
  qs('#btnUpdate').disabled = false;
  qs('#btnDelete').disabled = false;
}
function readForm(){
  return {
    regNo: qs('#regNo').value.trim(),
    firstName: qs('#firstName').value.trim(),
    lastName: qs('#lastName').value.trim(),
    dob: qs('#dob').value||null,
    gender: qs('#gender').value||null,
    contact: qs('#contact').value||null,
    program: qs('#program').value||null,
    status: qs('#status').value||'ACTIVE'
  };
}
function clearForm(){
  selectedId=null;
  qsa('input').forEach(i=>{ if(i.id!=='q') i.value=''; });
  qs('#status').value='ACTIVE';
  qs('#btnUpdate').disabled=true; qs('#btnDelete').disabled=true;
  qsa('#tbl tr').forEach(r=>r.classList.remove('sel'));
}
qs('#btnCreate').onclick = async ()=>{
  try{ await API.post('/api/students', readForm()); toast('Created'); clearForm(); loadStudents(); }catch(e){ toast(e.message,'err'); }
};
qs('#btnUpdate').onclick = async ()=>{
  if(!selectedId) return toast('Select a student','err');
  try{ await API.put(`/api/students/${selectedId}`, readForm()); toast('Updated'); clearForm(); loadStudents(); }catch(e){ toast(e.message,'err'); }
};
qs('#btnDelete').onclick = async ()=>{
  if(!selectedId) return toast('Select a student','err');
  if(!confirm('Delete selected student?')) return;
  try{ await API.del(`/api/students/${selectedId}`); toast('Deleted'); clearForm(); loadStudents(); }catch(e){ toast(e.message,'err'); }
};
qs('#btnClear').onclick = clearForm;
qs('#btnSearch').onclick = ()=> loadStudents(qs('#q').value);
qs('#btnReload').onclick = ()=>{ qs('#q').value=''; loadStudents(); }
loadStudents();
