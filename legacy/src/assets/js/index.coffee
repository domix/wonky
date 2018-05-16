body = document.body

select = body.querySelector('select')
input = body.querySelector('input')
button = body.querySelector('button')

button.className = ''
# capture submit
body.addEventListener 'submit', (ev) ->
  ev.preventDefault()
  button.disabled = true
  button.className = ''
  button.innerHTML = 'Por favor espera...'

  invite input.value

  return

invite = (email) ->
  $.ajax
    url: '/'
    type: 'POST'
    dataType: 'json'
    contentType: 'application/json; charset=utf-8'
    data: JSON.stringify(email: email)
    success: (data) ->
      if(data.ok)
        button.className = 'success'
        button.innerHTML = data.message
      else
        button.removeAttribute 'disabled'
        button.className = 'error'
        button.innerHTML = data.error
      return
    error: (xhr, type) ->
      console.log '%s %s', xhr, type
      return