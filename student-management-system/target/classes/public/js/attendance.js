let currentRows=[];
qs('#btnCreateSession').onclick = async ()=>{
  try{
    const offeringId = Number(qs('#offeringId').value);
    const date = qs('#date').value || new Date().toISOString().slice(0,10);
    const periodNo = Number(qs('#periodNo').value||1);
    const s = await API.post('/api/attendance/sessions',{offeringId,date,periodNo});
    qs('#sessionInfo').textContent = `Created session #${s.id}`;
    qs('#sessionId').value = s.id;
    toast('Session created');
  }catch(e){ toast(e.message,'err'); }
};
qs('#btnLoad').onclick = async ()=>{
  const offeringId = Number(qs('#offeringId').value);
  if(!offeringId) return toast('Enter offeringId','err');
  try{
    const enrollments = await API.get(`/api/enrollments?offeringId=${offeringId}`);
    const tb = qs('#tbl tbody'); tb.innerHTML='';
    currentRows = enrollments.map(e=>({studentId:e.studentId,status:'P',remarks:''}));
    for(const r of currentRows){
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${r.studentId}</td><td><select data-sid="${r.studentId}"><option value="P" selected>P</option><option value="A">A</option><option value="L">L</option></select></td><td><input type="text" data-rid="${r.studentId}" placeholder="Remarks"/></td>`;
      tb.appendChild(tr);
    }
    qsa('select[data-sid]').forEach(sel=>sel.onchange=e=>{ const id=Number(e.target.dataset.sid); const row=currentRows.find(x=>x.studentId===id); row.status=e.target.value; });
    qsa('input[data-rid]').forEach(inp=>inp.oninput=e=>{ const id=Number(e.target.dataset.rid); const row=currentRows.find(x=>x.studentId===id); row.remarks=e.target.value; });
  }catch(e){ toast(e.message,'err'); }
};
qs('#btnAllP').onclick = ()=>{ qsa('select[data-sid]').forEach(sel=>{ sel.value='P'; sel.dispatchEvent(new Event('change')); }); };
qs('#btnSubmit').onclick = async ()=>{
  const sessionId = Number(qs('#sessionId').value);
  if(!sessionId) return toast('Enter sessionId','err');
  try{ await API.post(`/api/attendance/${sessionId}/entries`, currentRows); toast('Saved attendance'); }catch(e){ toast(e.message,'err'); }
};
qs('#btnSummary').onclick = async ()=>{
  const sid = Number(qs('#sumStudentId').value);
  if(!sid) return toast('Enter student id','err');
  try{ const s = await API.get(`/api/attendance/summary?studentId=${sid}`); qs('#sumOut').textContent = `Present ${s.presentCount}/${s.totalCount} (${s.percent}%)`; }catch(e){ toast(e.message,'err'); }
};
