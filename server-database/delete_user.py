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

def run_delete_user(username,tb,valid):
   query = {'user_id' : username} 
   try:
      result = tb.remove(query)
   except Exception as e:
      valid = False
      print(str(e))
      return None
