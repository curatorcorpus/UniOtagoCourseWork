// Upgrade NOTE: replaced '_Object2World' with 'unity_ObjectToWorld'

Shader "Custom/Geometry" 
{
	
	Properties
	{
		voxel_size("voxel_size", float) = 1.0
	}

	SubShader {
		Tags
		{ "Queue" = "Geometry" "RenderType" = "Opaque" }
		Pass 
		{

			Tags{ "LightMode" = "ForwardBase" }
			LOD 200

			CGPROGRAM
			
			#pragma vertex vert
			#pragma geometry geo
			#pragma fragment frag
			#pragma multi_compile_fwdbase

			#include "UnityCG.cginc"
			#include "AutoLight.cginc"

			struct vs_in 
			{
				float4 pos: POSITION;
				float4 colour: COLOR;
			};

			struct vs_out 
			{
				float4 pos: POSITION;
				float4 colour: COLOR;
			};

			struct fs_in 
			{
				float4 pos: SV_POSITION;		// has to be called this way because of Unity Macro for light
				float4 colour: COLOR;

				LIGHTING_COORDS(1,2)
			};

			uniform float4 _LightColor0;
			float voxel_size;

			//VERTEX-------------------------------------------------------------------------------------------

			vs_out vert(vs_in v) {

				vs_out vout;
				
				vout.pos = mul(unity_ObjectToWorld, v.pos);

				vout.colour = v.colour;

				return vout;
			}

			//GEOMETRY-----------------------------------------------------------------------------------------

			[maxvertexcount(24)]
			void geo(point vs_out voxelPoint[1], inout TriangleStream<fs_in> stream) 
			{
				fs_in v1, v2, v3, v4, v5, v6, v7, v8;
				float4 center = voxelPoint[0].pos;
				float4 colour = voxelPoint[0].colour;
                float voxel_size_half = voxel_size / 2;
    
				v1.pos = float4(center.x + voxel_size_half, center.y + voxel_size_half, center.z + voxel_size_half, center.w);
				v1.pos = mul(UNITY_MATRIX_VP, v1.pos);
				v1.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v1);

				v2.pos = float4(center.x - voxel_size_half, center.y + voxel_size_half, center.z + voxel_size_half, center.w);
				v2.pos = mul(UNITY_MATRIX_VP, v2.pos);
				v2.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v2);

				v3.pos = float4(center.x + voxel_size_half, center.y - voxel_size_half, center.z + voxel_size_half, center.w);
				v3.pos = mul(UNITY_MATRIX_VP, v3.pos);
				v3.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v3);

				v4.pos = float4(center.x - voxel_size_half, center.y - voxel_size_half, center.z + voxel_size_half, center.w);
				v4.pos = mul(UNITY_MATRIX_VP, v4.pos);
				v4.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v4);

				v5.pos = float4(center.x + voxel_size_half, center.y + voxel_size_half, center.z - voxel_size_half, center.w);
				v5.pos = mul(UNITY_MATRIX_VP, v5.pos);
				v5.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v5);

				v6.pos = float4(center.x - voxel_size_half, center.y + voxel_size_half, center.z - voxel_size_half, center.w);
				v6.pos = mul(UNITY_MATRIX_VP, v6.pos);
				v6.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v6);

				v7.pos = float4(center.x + voxel_size_half, center.y - voxel_size_half, center.z - voxel_size_half, center.w);
				v7.pos = mul(UNITY_MATRIX_VP, v7.pos);
				v7.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v7);

				v8.pos = float4(center.x - voxel_size_half, center.y - voxel_size_half, center.z - voxel_size_half, center.w);
				v8.pos = mul(UNITY_MATRIX_VP, v8.pos);
				v8.colour = colour;
				TRANSFER_VERTEX_TO_FRAGMENT(v8);

				stream.Append(v1);
				stream.Append(v2);
				stream.Append(v3);
				stream.Append(v4);
				stream.RestartStrip();

				stream.Append(v2);
				stream.Append(v6);
				stream.Append(v4);
				stream.Append(v8);
				stream.RestartStrip();

				stream.Append(v6);
				stream.Append(v5);
				stream.Append(v8);
				stream.Append(v7);
				stream.RestartStrip();

				stream.Append(v5);
				stream.Append(v1);
				stream.Append(v7);
				stream.Append(v3);
				stream.RestartStrip();

				stream.Append(v5);
				stream.Append(v6);
				stream.Append(v1);
				stream.Append(v2);
				stream.RestartStrip();

				stream.Append(v3);
				stream.Append(v4);
				stream.Append(v7);
				stream.Append(v8);
				stream.RestartStrip();
			}

			//FRAGMENT-----------------------------------------------------------------------------------------

			float4 frag(fs_in v) : COLOR {
				// TODO: Shade based on lighting, probably using 
				// from http://docs.unity3d.com/Manual/SL-BuiltinIncludes.html
				float3 lightDirection = normalize(_WorldSpaceLightPos0.xyz);
				float atten = LIGHT_ATTENUATION(v);

				float3 ambient = UNITY_LIGHTMODEL_AMBIENT.xyz;
				float3 normal = float3(0,1,0);
				float3 lambert = float(max(0.0,dot(normal,lightDirection)));
				float3 lighting = (ambient + lambert * atten) * _LightColor0.rgb;

				float4 color = float4(v.colour * lighting, 1.0f);
				return color;
			}

			ENDCG
		}
	}
}
