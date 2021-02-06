import pymongo

"""
TODO: 
INPUTS: 
   username,password: string
   tb: database collection contains all users
   valid: set to false if error occurs
OUTPUTS:
   success: 1 if the user exists in database
            0 wrong username,password pair     
"""

def run_login(username,password,tb,valid):
   query = {'user_id' : username, 'password' : password} 
   try:
      result = tb.find(query)
      if result:
         return 1
      else:
         return 0
   except Exception as e:
      valid = False
      print(str(e))
      return None