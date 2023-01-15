import flask
import firebase_admin
from firebase_admin import credentials, firestore
from flask import Flask, request, jsonify

cred = credentials.Certificate('studybuddy-d48ac-firebase-adminsdk-u4i21-e40011517f.json')
app = firebase_admin.initialize_app(cred)
db = firestore.client()

app = flask.Flask(__name__)

def add_student(data):
    str = data.replace("add_student:", "", 1)
    list = str.split('_')
    name= list[0]
    year = list[1]
    degree = list[2]
    gender = list[3]
    age= list[4]
    phone= list[5]
    id = list[6]
    print(list)
    doc_ref = db.collection('students').document(id)
    doc_ref.set({
        'id': id,
        'age': age,
        'name': name,
        'degree': degree,
        'gender': gender,
        'phone': phone,
        'year': year
    })
    print(doc_ref.id)
    return "Updated Profile successfully"

def add_teacher(data):
    str = data.replace("add_teacher:", "", 1)
    list = str.split('_')
    name= list[0]
    year = list[1]
    degree = list[2]
    gender = list[3]
    age= list[4]
    phone= list[5]
    id = list[7]
    textPayBox = list[6]
    print(list)
    doc_ref = db.collection('teachers').document(id)
    doc_ref.set({
        'id': id,
        'age': age,
        'name': name,
        'degree': degree,
        'gender': gender,
        'phone': phone,
        'year': year,
        'textPayBox': textPayBox
    })
    print(doc_ref.id)
    return "Updated Profile successfully"



@app.route('/', methods=['GET', 'POST'])
def handle_request1():

    data = str(request.data, 'utf-8')
    print(str(request.data, 'utf-8'))


    if data.startswith("add_student:"):
        res = add_student(data)

    if data.startswith("add_teacher:"):
        res = add_teacher(data)

    return res

if __name__ == '__main__':

    app.run(host="0.0.0.0", port=5000, debug=True)
