
export const createGoogleForm = async (language) => {
  const formData = new URLSearchParams();
  formData.append('token', API_TOKEN);
  formData.append('language', language); // ✅ Thêm dòng này

  let rawResponseText = ''; 

  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData.toString(),
    });

    rawResponseText = await response.text();

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}. Response: ${rawResponseText}`);
    }

    try {
      const data = JSON.parse(rawResponseText);
      if (data && data.formUrl) {
        return data.formUrl;
      } else {
        throw new Error('API succeeded but returned no formUrl in JSON.');
      }
    } catch (parseError) {
      const urlMatch = rawResponseText.match(/(https?:\/\/[^\s]+)/);
      if (urlMatch && urlMatch[0]) {
        return urlMatch[0].trim();
      } else if (rawResponseText.startsWith('Tạo Form thành công!')) {
        const idMatch = rawResponseText.match(/Form ID: ([^\s]+)/);
        if (idMatch && idMatch[1]) {
          return `SUCCESS! Form ID: ${idMatch[1].trim()}. (Check console for raw response.)`;
        }
        return rawResponseText.trim();
      } else {
        throw new Error(`Invalid format. Backend returned: "${rawResponseText.substring(0, 30)}..."`);
      }
    }
  } catch (error) {
    console.error('Error in createGoogleForm service:', error);
    throw new Error(`Failed to create quiz: ${error.message}`);
  }
};
