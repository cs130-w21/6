./run_server.sh
  Produces two tensorflow servers in two dockers,
      inception_model: which convert an (299,299,3) image into latent vector
        with Inception_V3 preprocess model.
        
      autoencoder_model: the encoder-decoder algorithm that returns a
         generated quote. We need to de-tokenize to text after.

main.py
   modify image_path and num_quotes to get correct input_image
     and number of quotes generated for the single image.